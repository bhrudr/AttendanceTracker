package com.bhaskar.attendancetracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    List<Attendance> attendanceList;
    private AttendanceAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AttendanceAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public AttendanceAdapter(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public AttendanceAdapter.AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_report_card_design, parent, false);
        AttendanceAdapter.AttendanceViewHolder attendanceViewHolder = new AttendanceAdapter.AttendanceViewHolder(view, mListener);
        return attendanceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.AttendanceViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.name.setText(attendance.getName());
        holder.tokenNo.setText(attendance.getTokenNo());
        holder.attendanceType.setText(attendance.getAttendanceType());
        holder.timeReported.setText(attendance.getReportedTime());
        holder.otRequisitionTimeFrom.setText(attendance.getOtRequisitionFromTime());
        holder.getOtRequisitionTimeTo.setText(attendance.getOtRequisitionToTime());
        holder.timeReleased.setText(attendance.getReleasedTime());
        holder.description.setText(attendance.getDescription());
        holder.givenBy.setText(attendance.getGivenBy());
        holder.editedBy.setText(attendance.getEditedBy());
        holder.approvedBy.setText(attendance.getApprovedBy());
        holder.collectedBy.setText(attendance.getCollectedBy());
        holder.status.setText(attendance.getStatus());
        if (attendance.getStatus().equals("APPROVED")) {
            holder.approvedByLinearLayout.setVisibility(View.VISIBLE);
            holder.collectedByLinearLayout.setVisibility(View.INVISIBLE);
            holder.status.setTextColor(Color.GREEN);
        } else if(attendance.getStatus().equals("COLLECTED")) {
            holder.approvedByLinearLayout.setVisibility(View.VISIBLE);
            holder.collectedByLinearLayout.setVisibility(View.VISIBLE);
            holder.status.setTextColor(Color.parseColor("#056608"));
        } else {
            holder.approvedByLinearLayout.setVisibility(View.INVISIBLE);
            holder.collectedByLinearLayout.setVisibility(View.INVISIBLE);
            holder.status.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView name, tokenNo, attendanceType, timeReported, otRequisitionTimeFrom,
                getOtRequisitionTimeTo, timeReleased, description, givenBy, approvedBy, status, editedBy, collectedBy;
        LinearLayout approvedByLinearLayout, collectedByLinearLayout;

        public AttendanceViewHolder(@NonNull View itemView, final AttendanceAdapter.OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.attendance_report_employee_name);
            tokenNo = itemView.findViewById(R.id.attendance_report_employee_token);
            attendanceType = itemView.findViewById(R.id.attendance_report_attendance_type_text);
            timeReported = itemView.findViewById(R.id.attendance_report_time_reported_text);
            otRequisitionTimeFrom = itemView.findViewById(R.id.attendance_report_time_ot_requi_from_text);
            getOtRequisitionTimeTo = itemView.findViewById(R.id.attendance_report_time_ot_requi_to_text);
            timeReleased = itemView.findViewById(R.id.attendance_report_time_released_text);
            description = itemView.findViewById(R.id.attendance_report_desc_text);
            givenBy = itemView.findViewById(R.id.attendance_report_given_by_text);
            editedBy = itemView.findViewById(R.id.attendance_report_edited_by_text);
            approvedBy = itemView.findViewById(R.id.attendance_report_approved_by_text);
            status = itemView.findViewById(R.id.attendance_report_attendance_status);
            approvedByLinearLayout = itemView.findViewById(R.id.attendance_report_approved_by_linear_layout);
            collectedBy = itemView.findViewById(R.id.attendance_report_collected_by_text);
            collectedByLinearLayout = itemView.findViewById(R.id.attendance_report_collected_by_linear_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public void filterAttendanceList(List<Attendance> filteredList) {
        attendanceList = filteredList;
        notifyDataSetChanged();
    }
}
