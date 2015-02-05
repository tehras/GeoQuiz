package com.example.taras.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends ActionBarActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATER_BOOLEAN = "cheater_boolean";
    private static final String KEY_CHEATED_INDEX = "cheated_index";
    private static final String KEY_NEXT_PRESSED = "next_pressed";
    private static final String KEY_PREV_PRESSED = "prev_pressed";

    //Buttons
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;

    //Text Views
    private TextView mQuestionTextView;

    //Question Bank
    private TrueFalse[] mQuestionBank = new TrueFalse[]{
            new TrueFalse(R.string.question_oceans, true),
            new TrueFalse(R.string.question_mideast, false),
            new TrueFalse(R.string.question_africa, false),
            new TrueFalse(R.string.question_americas, true),
            new TrueFalse(R.string.question_asia, true),
    };

    //Track Current Question
    private int mCurrentIndex = 0;
    private int mCheatedQuestion;
    private boolean mNextPassed = false;
    private boolean mPrevPassed = false;

    private boolean mIsCheater;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        mIsCheater = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Set The Question
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        //.Set The Question

        mTrueButton = (Button) findViewById(R.id.true_button);
        //Set Listener for True Button
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        //Set Listener for False Button
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCheater) {
                    if (mNextPassed && mCurrentIndex == mCheatedQuestion) {
                        Toast.makeText(QuizActivity.this, "Answer The Question Correctly First", Toast.LENGTH_SHORT).show();
                    } else {
                        mNextPassed = true;
                        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                        updateQuestion();
                    }
                } else {
                    mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    updateQuestion();
                }
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCheater) {
                    if (mPrevPassed && mCurrentIndex == mCheatedQuestion) {
                        Toast.makeText(QuizActivity.this, "Answer The Question Correctly First", Toast.LENGTH_SHORT).show();
                    } else {
                        mPrevPassed = true;
                        if (mCurrentIndex == 0) {
                            mCurrentIndex = mQuestionBank.length - 1;
                        } else {
                            mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                        }
                        updateQuestion();
                    }
                } else {
                    if (mCurrentIndex == 0) {
                        mCurrentIndex = mQuestionBank.length - 1;
                    } else {
                        mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    }
                    updateQuestion();
                }
            }
        });

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCheatedQuestion = savedInstanceState.getInt(KEY_CHEATED_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER_BOOLEAN, false);
            mNextPassed = savedInstanceState.getBoolean(KEY_NEXT_PRESSED, false);
            mPrevPassed = savedInstanceState.getBoolean(KEY_PREV_PRESSED, false);
        }

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Cheat Activity
                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);

                //Send isTrue as extra in the intent
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
                intent.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);

                mCheatedQuestion = mCurrentIndex;

                //The second int will define which children activity it is. in this case we use 0 to define the child activity
                startActivityForResult(intent, 0);
            }
        });

        updateQuestion();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstance");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_CHEATER_BOOLEAN, mIsCheater);
        savedInstanceState.putInt(KEY_CHEATED_INDEX, mCheatedQuestion);
        savedInstanceState.putBoolean(KEY_NEXT_PRESSED, mNextPassed);
        savedInstanceState.putBoolean(KEY_PREV_PRESSED, mPrevPassed);
    }

    /**
     * Checks if the user got the answer correct
     *
     * @param userPressedTrue
     */
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();

        int messageResId = 0;

        if (mCurrentIndex == mCheatedQuestion && (mPrevPassed || mNextPassed)) {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                resetCheated();
            } else {
                messageResId = R.string.incorrect_toast;
            }
        } else if (mIsCheater && mCurrentIndex == mCheatedQuestion) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void resetCheated() {
        mIsCheater = false;
        mNextPassed = false;
        mPrevPassed = false;
        mCheatedQuestion = 0;
    }

    /**
     * Updates the question
     */

    private void updateQuestion() {
//        Log.d(TAG, "Updating question text for question #" + mCurrentIndex, new Exception());
        int question = mQuestionBank[mCurrentIndex].getQuestion();
        mQuestionTextView.setText(question);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(Bundle) called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause(Bundle) called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume(Bundle) called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(Bundle) called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy(Bundle) called");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
