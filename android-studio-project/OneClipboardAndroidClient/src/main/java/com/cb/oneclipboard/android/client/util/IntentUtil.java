package com.cb.oneclipboard.android.client.util;

import com.cb.oneclipboard.android.client.HomePageActivity;

import android.content.Context;
import android.content.Intent;

public class IntentUtil {
  
  public static Intent getHomePageIntent( Context context ) {
    Intent homePageIntent = new Intent( context, HomePageActivity.class );
    homePageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    
    return homePageIntent;
  }
}
