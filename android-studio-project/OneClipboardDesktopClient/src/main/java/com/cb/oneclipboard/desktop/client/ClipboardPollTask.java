package com.cb.oneclipboard.desktop.client;

import com.cb.oneclipboard.lib.client.Callback;

public class ClipboardPollTask implements Runnable{
  private TextTransfer textTransfer;
  private Callback callback;
  private String clipboardContent;
  
  public ClipboardPollTask(TextTransfer textTransfer, Callback callback){
    this.textTransfer     = textTransfer;
    this.callback         = callback;
    this.clipboardContent = textTransfer.getClipboardContents();
  }
  
  @Override
  public void run() {
    String content = textTransfer.getClipboardContents();
    if(content != null && !content.equals(clipboardContent)){
      clipboardContent = content;
      callback.execute(content);
    }
  }
  
  public void updateClipboardContent(String newContent){
    this.clipboardContent = newContent;
  }
}
