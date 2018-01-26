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

package com.mayabot.nlp.segment.segment;

import com.mayabot.nlp.segment.MynlpSegment;
import com.mayabot.nlp.segment.MynlpTerm;
import com.mayabot.nlp.utils.Characters;

/**
 * 过滤标点符号
 */
public class PunctuationFilter extends WrapMySegmentFilter {

    @Override
    boolean accept(MynlpTerm term) {
        return !Characters.isPunctuation(term.word.charAt(0));
    }

    public PunctuationFilter(MynlpSegment myAnalyzer) {
        super(myAnalyzer);
    }
}
