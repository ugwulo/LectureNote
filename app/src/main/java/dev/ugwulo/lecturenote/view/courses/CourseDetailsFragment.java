package dev.ugwulo.lecturenote.view.courses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.FragmentCourseDetailsBinding;
import dev.ugwulo.lecturenote.model.Message;
import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.util.StoragePaths;
import dev.ugwulo.lecturenote.view.MainActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment {@link CourseDetailsFragment} to display course contents for a particular course
 */
public class CourseDetailsFragment extends Fragment implements View.OnClickListener{

    FragmentCourseDetailsBinding mCourseDetailsBinding;
    StoragePaths mStoragePaths = new StoragePaths();
    private String saveCurrentTime, saveCurrentDate;
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private String mCourseCode;
    private String checker = "", messageUrl = "";
    private Uri fileUri;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    private StorageTask uploadTask;
    private final int fileRequestCode = 200;
    private ProgressDialog mProgressDialog;
    public CourseDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCourseCode = getArguments().getString(getString(R.string.course_code_bundle));
        }

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.course_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mCourseDetailsBinding.toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mCourseDetailsBinding.appbarTitle.setText(mCourseCode);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCourseDetailsBinding = FragmentCourseDetailsBinding.inflate(getLayoutInflater());

        showDefaultView();
        mCourseDetailsBinding.sendMessage.setOnClickListener(this);
        return mCourseDetailsBinding.getRoot();
    }

    /**  showDefaultView() displays a default view when no notes are added **/
    private void showDefaultView() {
        if (Settings.isStudentLogin() && Settings.isFirstTimeLaunch()) {
            mCourseDetailsBinding.studentDefaultView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.sendFile.setVisibility(View.GONE);
        } else if (Settings.isStudentLogin() && !Settings.isFirstTimeLaunch()) {
            mCourseDetailsBinding.mainView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.sendMsgBackground.setVisibility(View.VISIBLE);
        }

        if (Settings.isLecturerLogin() && Settings.isFirstTimeLaunch()){
            mCourseDetailsBinding.lecturerDefaultView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.addNotes.setOnClickListener(this);
        } else if (Settings.isLecturerLogin() && !Settings.isFirstTimeLaunch()){
            mCourseDetailsBinding.mainView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.sendMsgBackground.setVisibility(View.VISIBLE);
        }
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        mReference.child(getString(R.string.dbnode_discussion)).child(messageSenderID).child(messageReceiverID)
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                    {
//                        Message message = dataSnapshot.getValue(Message.class);
//                        messagesList.add(message);
//                        messageAdapter.notifyDataSetChanged();
//                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                    {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
//                    {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
//                    {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError)
//                    {
//
//                    }
//                });
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_notes:
                uploadLectureNotes();
                break;
            case R.id.send_message:
                sendMessage();
        }
    }

    private void sendMessage() {
//        TODO handle messages
    }

    /** handles note file/attachment upload by a lecturer **/
    private void uploadLectureNotes() {
        CharSequence[] options = new CharSequence[]
                {
                        "Images",
                        "Pdf Files",
                        "MS Word Files"
                };

        // show dialog for file type selection **/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select the File");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    checker = "image";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select Image(s)"), fileRequestCode);
                }
                if(i==1)
                {
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent.createChooser(intent,"Select PDF"), fileRequestCode);
                }
                if(i==2)
                {
                    checker = "docx";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/msword");
                    startActivityForResult(intent.createChooser(intent,"Select MS-Word File"), fileRequestCode);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == fileRequestCode && resultCode == RESULT_OK && data != null && data.getData() != null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Uploading Note File");
            mProgressDialog.setMessage("...please wait while your file is uploaded");
            mProgressDialog.setCanceledOnTouchOutside(false);

            fileUri = data.getData();
            if (!checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mStoragePaths.NOTE_IMAGE)
                        .child(mCourseCode);
                final String messageSenderRef = getString(R.string.dbnode_discussion) + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = getString(R.string.dbnode_discussion) + messageReceiverID + "/" + messageSenderID;
                DatabaseReference userMessageKeyRef = mReference.child(getString(R.string.dbnode_discussion))
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", messageUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            if (checker.equals("pdf")) {
                                messageTextBody.put("type", checker);
                            }
                            else if (checker.equals("docx")) {
                                messageTextBody.put("type", checker);
                            }
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody );
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            mReference.updateChildren(messageBodyDetails);
                            mProgressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        mProgressDialog.setMessage((int)p + "% complete");
                    }
                });
            }

            else if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mStoragePaths.NOTE_IMAGE);
                final String messageSenderRef = getString(R.string.dbnode_discussion) + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = getString(R.string.dbnode_discussion) + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = mReference.child(getString(R.string.dbnode_discussion))
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUrl = task.getResult();
                        messageUrl = downloadUrl.toString();

                        Map<String, String> messageTextBody = new HashMap<String, String>();
                        messageTextBody.put("message", messageUrl);
                        messageTextBody.put("name", fileUri.getLastPathSegment());
                        messageTextBody.put("type", checker);
                        messageTextBody.put("from", messageSenderID);
                        messageTextBody.put("to", messageReceiverID);
                        messageTextBody.put("messageID", messagePushID);
                        messageTextBody.put("time", saveCurrentTime);
                        messageTextBody.put("date", saveCurrentDate);

                        Map<String, Object> messageBodyDetails = new HashMap<>();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                        mReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                }
                                mProgressDialog.dismiss();

                            }
                        });
                    }
                });
            }
            else {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(),"nothing selected,error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}