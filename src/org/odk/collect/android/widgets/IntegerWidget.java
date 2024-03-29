/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import org.javarosa.core.model.condition.pivot.IntegerRangeHint;
import org.javarosa.core.model.condition.pivot.StringLengthRangeHint;
import org.javarosa.core.model.condition.pivot.UnpivotableExpressionException;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.IntegerData;
import org.javarosa.core.model.data.LongData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.listeners.WidgetChangedListener;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * Widget that restricts values to integers.
 * 
 * @author Carl Hartung (carlhartung@gmail.com)
 */
public class IntegerWidget extends StringWidget {
	
	//1 for int. 0 for long?
	int number_type;

    public IntegerWidget(Context context, FormEntryPrompt prompt, boolean secret, int num_type) {
        super(context, prompt, secret);

        mAnswer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mAnswerFontsize);
        
        this.number_type=num_type;

        // needed to make long readonly text scroll
        mAnswer.setHorizontallyScrolling(false);
        if(!secret) {
        	mAnswer.setSingleLine(false);
        }

        // only allows numbers and no periods
        mAnswer.setKeyListener(new DigitsKeyListener(true, false));

        
        //We might have 
        
        if (prompt.isReadOnly()) {
            setBackgroundDrawable(null);
            setFocusable(false);
            setClickable(false);
        }
        
        if (getCurrentAnswer() != null){
        	if(number_type==1){
        		Integer i = (Integer) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        	else{
        		Long i= (Long) getCurrentAnswer().getValue();
        		if (i != null) {
                    mAnswer.setText(i.toString());
                }
        	}
        }
    }
    
    
    @Override
    protected int guessMaxStringLength(FormEntryPrompt prompt) throws UnpivotableExpressionException{
    	int existingGuess = Integer.MAX_VALUE;
    	try {
    		existingGuess = super.guessMaxStringLength(prompt);
    	} catch (UnpivotableExpressionException e) {
    		
    	}
    	try {
			//Awful. Need factory for this
			IntegerRangeHint hint = new IntegerRangeHint();
			prompt.requestConstraintHint(hint);
			
			IntegerData maxexample = hint.getMax();
			IntegerData minexample = hint.getMin();
			
			if(minexample != null) {
				//If we didn't constrain the input to be 0 or more, don't bother
				if(((Integer)minexample.getValue()).intValue() < 0) {
					throw new UnpivotableExpressionException(); 
				}
			} else {
				//could be negative. Not worth it
				throw new UnpivotableExpressionException();
			}
			
			if(maxexample != null) {
				int max = ((Integer)maxexample.getValue()).intValue();
				if(!hint.isMaxInclusive()) {
					max -= 1;
				}
				return Math.min(existingGuess, String.valueOf(max).length());
			}					
		} catch(Exception e) {

		}
        if(number_type==1){
        	return Math.min(existingGuess, 9);
        } else {
        	return Math.min(existingGuess, 15);
        }
    }
    
    @Override
    protected void setTextInputType(EditText mAnswer) {
    	if(secret) {
        	mAnswer.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        	mAnswer.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    @Override
    public IAnswerData getAnswer() {
        String s = mAnswer.getText().toString().trim();
        if (s == null || s.equals("")) {
            return null;
        } else {
            try {
            	if(number_type==1){
            		return new IntegerData(Integer.parseInt(s));
            	}
            	else{
            		return new LongData(Long.parseLong(s));
            	}
            } catch (Exception NumberFormatException) {
                return null;
            }
        }
    }

}
