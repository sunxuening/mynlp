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
package com.mayabot.nlp.segment.analyzer;

import com.mayabot.nlp.segment.WordTerm;

import java.util.function.Predicate;

/**
 * @author jimichan
 */
public abstract class FilterWordTermGenerator extends BaseWordTermGeneratorWraper implements Predicate<WordTerm> {

    public FilterWordTermGenerator(WordTermGenerator base) {
        super(base);
    }

    @Override
    public WordTerm nextWord() {


        WordTerm next = base.nextWord();
        while (next != null) {
            if (test(next)) {
                return next;
            } else {
                next = base.nextWord();
            }
        }
        return null;

    }

}
