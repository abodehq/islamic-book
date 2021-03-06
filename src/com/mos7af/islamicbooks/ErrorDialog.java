package com.mos7af.islamicbooks;

import java.io.PrintWriter;  
import java.io.StringWriter;  
import android.app.Activity;  
import android.app.AlertDialog;  
import android.app.Dialog;  
import android.content.Context;  
import android.content.DialogInterface;  
import android.text.method.ScrollingMovementMethod;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.TextView;  
public class ErrorDialog {  
     private String message;  
     private Context context;  
     private Exception exception;  
     public ErrorDialog(Context context, String message) {  
          this.context = context;  
          this.message = message;  
     }  
     public ErrorDialog(Context context, String message, Exception exception) {  
          this(context, message);  
          this.exception = exception;  
     }  
     public String getMessage() {  
          return message;  
     }  
     public void setMessage(String message) {  
          this.message = message;  
     }  
     public Context getContext() {  
          return context;  
     }  
     public void setContext(Context context) {  
          this.context = context;  
     }  
     public Exception getException() {  
          return exception;  
     }  
     public void setException(Exception exception) {  
          this.exception = exception;  
     }  
     public void show() {  
          if (exception == null) {  
               showWithoutException();  
          } else {  
               showWithException();  
          }  
     }  
     public void showWithoutException() {  
          AlertDialog.Builder builder = new AlertDialog.Builder(context);  
          builder.setMessage(message);  
          builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
               @Override  
               public void onClick(DialogInterface dialog, int which) {  
                    dialog.cancel();  
               }  
          });  
          AlertDialog alert = builder.create();  
          alert.show();  
     }  
     public void showWithException() {  
          AlertDialog.Builder builder;  
          LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
          View layout = inflater.inflate(R.layout.error, null);  
          TextView text = (TextView) layout.findViewById(R.id.error_text);  
          text.setMovementMethod(new ScrollingMovementMethod());  
          StringWriter sw = new StringWriter();  
          exception.printStackTrace(new PrintWriter(sw));  
          text.setText(sw.toString());  
          builder = new AlertDialog.Builder(context);  
          builder.setView(layout);  
          builder.setMessage(message);  
          builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
               @Override  
               public void onClick(DialogInterface dialog, int which) {  
                    dialog.cancel();  
               }  
          });  
          AlertDialog alert = builder.create();  
          alert.show();  
     }  
}  