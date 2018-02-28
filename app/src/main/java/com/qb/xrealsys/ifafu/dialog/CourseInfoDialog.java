package com.qb.xrealsys.ifafu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qb.xrealsys.ifafu.R;
import com.qb.xrealsys.ifafu.model.Course;

/**
 * Created by sky on 28/02/2018.
 */

public class CourseInfoDialog extends Dialog implements View.OnClickListener {

    private TextView    title;

    private TextView    teacher;

    private TextView    time;

    private TextView    location;

    private Button      closeBtn;

    public CourseInfoDialog(@NonNull Context context) {
        super(context, R.style.styleProgressDialog);
        setContentView(R.layout.dialog_display_course_info);

        title       = findViewById(R.id.courseInfoDialogTitle);
        teacher     = findViewById(R.id.courseInfoDialogTeacherName);
        time        = findViewById(R.id.courseInfoDialogTime);
        location    = findViewById(R.id.courseInfoDialogLocation);
        closeBtn    = findViewById(R.id.courseInfoDialogBtn);
        closeBtn.setOnClickListener(this);
    }

    public void show(Course course) {
        title.setText(course.getName());
        teacher.setText(course.getTeacher());
        time.setText(course.getTimeString());
        location.setText(course.getAddress());

        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courseInfoDialogBtn:
                this.cancel();
                break;
        }
    }
}
