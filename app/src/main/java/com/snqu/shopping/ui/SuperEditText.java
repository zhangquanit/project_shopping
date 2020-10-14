package com.snqu.shopping.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class SuperEditText extends AppCompatEditText {
    public SuperEditText(Context context) {
        super(context);
    }

    public SuperEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//
//
//        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
//            int x2 = ((int) event.getX()) - getTotalPaddingLeft();
//            int y = ((int) event.getY()) - getTotalPaddingTop();
//            int scrollX = x2 + getScrollX();
//            int scrollY = y + getScrollY();
//            Layout layout = getLayout();
//            int lineForVertical = layout.getLineForVertical(scrollY);
//            float f2 = (float) scrollX;
//            int offsetForHorizontal = layout.getOffsetForHorizontal(lineForVertical, f2);
//            Editable editableText = getEditableText();
//            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) editableText.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
//            if (clickableSpanArr != null && clickableSpanArr.length > 0 && scrollX >= 0 && f2 <= layout.getLineWidth(lineForVertical)) {
//                if (action == 1) {
//                    clickableSpanArr[0].onClick(this);
//                } else if (action == 0) {
//                    Selection.setSelection(editableText, editableText.getSpanStart(clickableSpanArr[0]), editableText.getSpanEnd(clickableSpanArr[0]));
//                    return super.onTouchEvent(event);
//                }
//                return true;
//            }
//
//            int selectionEnd = getSelectionEnd();
//            int selectionStart = getSelectionStart();
//            System.out.println("onTouchEvent selectionStart="+selectionStart+",selectionEnd="+selectionEnd);
//        }

        return super.onTouchEvent(event);

    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return super.onTextContextMenuItem(id);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        System.out.println("onSelectionChanged selStart="+selStart+",selEnd="+selEnd);
    }

    public void onAttachedToWindow() {
        setCursorVisible(true);
        super.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        setCursorVisible(false);
        super.onDetachedFromWindow();
    }
    public CharSequence getHint() {
        CharSequence hint = super.getHint();
        return hint == null ? "" : hint;
    }

}
