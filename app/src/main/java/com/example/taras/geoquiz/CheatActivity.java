package com.example.taras.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Taras on 2/3/2015.
 */
public class CheatActivity extends Activity {

    //EXTRA ANSWER IS TRUE
    public static final String EXTRA_ANSWER_IS_TRUE = "com.example.taras.geoquiz.answer_is_true";
    public static final String EXTRA_ANSWER_SHOWN = "com.exammple.taras.geoquiz.answer_shown";

    public static final String EXTRA_ANSWER_AFTER_ROTATE = "com.example.taras.geoquiz.answer_after_rotate";

    //Normal variables
    private boolean mAnswerIsTrue;
    private boolean mIsAnswerShown;

    private TextView mAnswerTextView;
    private Button mShowAnswer;

    private void setAnswerShownResult(boolean isAnswerShown) {
        mIsAnswerShown = isAnswerShown;
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = (TextView) findViewById(R.id.answerTextView);

        //At first set Answer Shown as False
        if(savedInstanceState != null) {
            setAnswerShownResult(savedInstanceState.getBoolean(EXTRA_ANSWER_AFTER_ROTATE));
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
        } else {
            // Answer will not be shown until the user presses the button
            setAnswerShownResult(false);
        }

        mShowAnswer = (Button) findViewById(R.id.showAnswerButton);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerShownResult(true);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(EXTRA_ANSWER_AFTER_ROTATE, mIsAnswerShown);
    }
}
