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

package com.mayabot.nlp.segment.wordnetiniter;

import com.google.common.collect.Lists;
import com.mayabot.nlp.segment.WordnetInitializer;
import com.mayabot.nlp.segment.wordnet.Wordnet;

import java.util.List;

public class MultiWordnetInit implements WordnetInitializer {

    private List<WordnetInitializer> list;

    public MultiWordnetInit(WordnetInitializer... list) {
        this.list = Lists.newArrayList(list);
    }

    @Override
    public void initialize(Wordnet wordnet) {
        for (WordnetInitializer wordnetInitializer : list) {
            wordnetInitializer.initialize(wordnet);
        }
    }
}
