package com.cb.oneclipboard.android.client.tasker.ui;

import com.cb.oneclipboard.android.client.R;
import com.cb.oneclipboard.android.client.tasker.TaskerIpc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

public class EditActivity extends Activity {

  private RadioGroup rg;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(R.string.app_name);
    getActionBar().setDisplayHomeAsUpEnabled(false);
    setContentView(R.layout.tasker_configuration);

    rg = (RadioGroup) findViewById(R.id.tasker_config_radiogroup_origin);

    prepopulate();
  }

  private void prepopulate() {
    Intent intent = getIntent();

    TaskerIpc.setCheckedRadioButtonFromConfigIntent(rg, intent);
  }

  @Override
  public void finish() {
    Intent resultIntent = TaskerIpc.getResultIntentFromRadioGroup(this, rg);
    setResult(RESULT_OK, resultIntent);

    super.finish();
  }

  @Override
  public void onBackPressed() {
    finish();

    super.onBackPressed();
  }

  @Override
  protected void onStop() {
    finish();

    super.onStop();
  }

}
