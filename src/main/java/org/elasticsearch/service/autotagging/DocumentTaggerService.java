package org.elasticsearch.service.autotagging;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.HashMultiset;
import org.elasticsearch.common.collect.Multiset;
import org.elasticsearch.common.collect.Multiset.Entry;
import org.elasticsearch.common.collect.Multisets;
import org.elasticsearch.common.collect.Sets;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class DocumentTaggerService extends AbstractLifecycleComponent<DocumentTaggerService> {

    private static ESLogger logger = ESLoggerFactory.getLogger(DocumentTaggerService.class.getName());
    private static MaxentTagger tagger;
    private final Environment environment;

    @Inject
    public DocumentTaggerService(Settings settings, Client client, Environment environment) {
        super(settings);
        this.environment = environment;
    }

    public Set<String> extractKeywords(String text) {
        TokenizerFactory<CoreLabel> ptbTokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "untokenizable=noneKeep");
        BufferedReader r = new BufferedReader(new StringReader(text));
        DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
        documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
        Multiset<TaggedWord> nounSet = HashMultiset.create();
        for (List<HasWord> sentence : documentPreprocessor) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);
            for (TaggedWord word : tSentence) {
                if (word.tag().matches("NN|NNP|NNS|NNPS")) {
                    nounSet.add(word);
                }
            }
        }

        Set<String> keywords = Sets.newHashSet();
        int max = 10;
        int count = 0;
        Multiset<TaggedWord> sortedSet = Multisets.copyHighestCountFirst(nounSet);
        Iterator<Entry<TaggedWord>> iterator = sortedSet.entrySet().iterator();
        while (count < max && iterator.hasNext()) {
            Entry<TaggedWord> item = iterator.next();
            logger.debug("Add tag: {} - {}", item.getElement().value(), item.getCount());
            keywords.add(item.getElement().value());
            count++;
        }
        return keywords;

    }

    @Override
    protected void doStart() throws ElasticSearchException {
        logger.debug("doStart");
        String model = environment.pluginsFile().toPath().resolve("auto-tagging/models/wsj-0-18-left3words-distsim.tagger").toString();
        logger.info("Model path: {}", model);
        logger.debug("Settings: {}", settings);
        tagger = new MaxentTagger(model);
    }

    @Override
    protected void doStop() throws ElasticSearchException {
        logger.debug("doStop");
    }

    @Override
    protected void doClose() throws ElasticSearchException {
        logger.debug("doClose");
    }
}
