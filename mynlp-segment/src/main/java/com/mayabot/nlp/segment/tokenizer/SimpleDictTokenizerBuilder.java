//package com.mayabot.nlp.segment.tokenizer;
//
//import com.mayabot.nlp.Mynlps;
//import com.mayabot.nlp.segment.PipelineTokenizerBuilder;
//import com.mayabot.nlp.segment.tokenizer.bestpath.LongpathBestPathAlgorithm;
//import com.mayabot.nlp.segment.tokenizer.collector.SentenceCollector;
//import com.mayabot.nlp.segment.tokenizer.xprocessor.CommonSplitAlgorithm;
//import com.mayabot.nlp.segment.tokenizer.xprocessor.CustomDictionaryProcessor;
//
///**
// * 简单快速版本的词典分词，路径选择最大词
// *
// * @author jimichan
// */
//public class SimpleDictTokenizerBuilder extends BaseTokenizerBuilder {
//
//    @Override
//    protected void setUp(PipelineTokenizerBuilder builder) {
//        setCorrection(false);
//
//        //wordnet初始化填充
//        builder.addWordSplitAlgorithm(
//                Mynlps.getInstance(BigramTokenizerBuilder.CoreDictionarySplitAlgorithm.class),
//                Mynlps.getInstance(CommonSplitAlgorithm.class)
//        );
//
//        builder.setBestPathComputer(LongpathBestPathAlgorithm.class);
//
//
//        builder.addProcessor(CustomDictionaryProcessor.class);
//
//        //结果收集器
//        builder.setTermCollector(Mynlps.getInstance(SentenceCollector.class));
//    }
//
//
//}
