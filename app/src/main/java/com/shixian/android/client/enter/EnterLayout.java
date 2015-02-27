package com.shixian.android.client.enter;

import android.app.Activity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.feeddate.AllItemType;
import com.shixian.android.client.model.feeddate.BaseFeed;

import java.util.HashMap;
import java.util.Map;


/**
 * s0ng
 */
public class EnterLayout {

    private Activity mActivity;

    public TextView sendText;
    public ImageButton send;
    public EditText content;
    private Object tag;

    public static Map<String, String> cacheMap = new HashMap<>();


    public EnterLayout(Activity activity, View.OnClickListener sendTextOnClick) {


        mActivity = activity;

        sendText = (TextView) activity.findViewById(R.id.sendText);
        sendText.setOnClickListener(sendTextOnClick);
        content = (EditText) activity.findViewById(R.id.comment);


        content.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendText.setBackgroundResource(R.drawable.edit_send_green);
                    sendText.setTextColor(0xffffffff);
                } else {
                    sendText.setBackgroundResource(R.drawable.edit_send);
                    sendText.setTextColor(0xff999999);
                }
            }
        });
        content.setText("");

    }


    public void hideKeyboard() {
        Global.popSoftkeyboard(mActivity, content, false);
    }

    public void popKeyboard() {
        content.requestFocus();
        Global.popSoftkeyboard(mActivity, content, true);
    }

    public void insertText(String s) {
        content.requestFocus();
        int insertPos = content.getSelectionStart();
        String insertString = s + " ";
        Editable editable = content.getText();
        editable.insert(insertPos, insertString);

//        content.setSelection(insertPos + insertString.length());
    }


    public void deleteOneChar() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        content.dispatchKeyEvent(event);
    }

    public void clearContent() {
        content.setText("");
    }

    public String getContent() {
        return content.getText().toString();
    }

    public void hide() {
        if (tag != null) {
            if (tag instanceof BaseFeed) {
                BaseFeed baseFeed = (BaseFeed) tag;

                String type;
                String key;
                if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                    type = ((Comment) baseFeed).commentable_type;
                    key = type + ((Comment) baseFeed).id;
                } else {
                    type = baseFeed.feedable_type;
                    key = type + baseFeed.id;
                }



                cacheMap.put(key, content.getText().toString() + "");

            }else if(tag instanceof AllItemType)
            {
                AllItemType allItemType = (AllItemType) tag;

                String type=allItemType.type;
                String key=allItemType.id;



                String text = cacheMap.get(key);
                if (text != null)
                    content.setText(text);

                cacheMap.put(key, content.getText().toString() + "");
            }


        }

        View root = mActivity.findViewById(R.id.commonEnterRoot);
        root.setVisibility(View.GONE);
        tag = null;

    }

    /**
     * @param tag
     */
    public void show(Object tag) {


        View root = mActivity.findViewById(R.id.commonEnterRoot);
        root.setVisibility(View.VISIBLE);
        this.tag = tag;


        if (tag instanceof BaseFeed) {
            BaseFeed baseFeed = (BaseFeed) tag;

            String type;
            String key;
            if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                type = ((Comment) baseFeed).commentable_type;
                key = type + ((Comment) baseFeed).id;
            } else {
                type = baseFeed.feedable_type;
                key = type + baseFeed.id;
            }

            String text = cacheMap.get(key);
            if (text != null)
                content.setText(text);
        }

       else if(tag instanceof AllItemType)
        {
            AllItemType allItemType = (AllItemType) tag;

            String type=allItemType.type;
            String key=allItemType.id;



            String text = cacheMap.get(key);
            if (text != null)
                content.setText(text);
        }


    }


//    public void restoreSaveStart() {
//        content.addTextChangedListener(restoreWatcher);
//    }
//
//    public void restoreSaveStop() {
//        content.removeTextChangedListener(restoreWatcher);
//    }

//    public void restoreDelete(Object comment) {
//        CommentBackup.getInstance().delete(CommentBackup.BackupParam.create(comment));
//    }

//    public void restoreLoad(Object object) {
//        if (object == null) {
//            return;
//        }
//
//        restoreSaveStop();
//        clearContent();
//        String lastInput = CommentBackup.getInstance().load(CommentBackup.BackupParam.create(object));
//        content.getText().append(lastInput);
//        restoreSaveStart();
//    }

//    private TextWatcher restoreWatcher = new SimpleTextWatcher() {
//        @Override
//        public void afterTextChanged(Editable s) {
//            Object tag = content.getTag();
//            if (tag == null) {
//                return;
//            }
//
//            CommentBackup.getInstance().save(CommentBackup.BackupParam.create(tag), s.toString());
//        }
//    };



    public Object getTag() {
        return tag;
    }

    //用于传送图片  这里没用
    public interface CameraAndPhoto {
        void photo();
    }


}
