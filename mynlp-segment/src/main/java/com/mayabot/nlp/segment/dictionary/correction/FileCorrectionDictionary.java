/*
 * Copyright 2018 mayabot.com authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mayabot.nlp.segment.dictionary.correction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mayabot.nlp.collection.dat.DoubleArrayTrieMap;
import com.mayabot.nlp.segment.dictionary.CorrectionDictionary;
import com.mayabot.nlp.segment.dictionary.CorrectionWord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.TreeMap;

/**
 * File版本CorrectionDictionary
 * 文件内容格式：
 * 第几套/房
 * <p>
 * 一行一个规则
 *
 * @author jimichan
 */
public class FileCorrectionDictionary implements CorrectionDictionary {

    private TreeMap<String, CorrectionWord> dict;

    private DoubleArrayTrieMap<CorrectionWord> trie;

    public FileCorrectionDictionary(File file, Charset charset) throws IOException {
        TreeMap<String, CorrectionWord> dict = Maps.newTreeMap();

        ImmutableList<String> lines = Files.asCharSource(file, charset).readLines();

        for (String line : lines) {

            CorrectionWord adjustWord = CorrectionWord.parse(line);

            dict.put(adjustWord.path, adjustWord);
        }

        trie = new DoubleArrayTrieMap<>(dict);
    }

    @Override
    public DoubleArrayTrieMap<CorrectionWord> getTrie() {
        return trie;
    }

}
