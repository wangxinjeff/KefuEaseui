package com.kefueaseui.kefueaseui.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.NewCommentBody;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.RecorderButton;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.hyphenate.util.DensityUtil;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.utils.FileUploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发起留言评论界面,
 */
public class NewCommentActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = NewCommentActivity.class.getSimpleName();
    protected static final int REQUEST_CODE_LOCAL = 3;
    private ImageButton ibBack;
    private Button btnSend;
    private RecorderButton recButton;
    private EditText editText;
    private TextView tvAddFile;
    private ProgressDialog pd;
    private String ticketId;
    private LinearLayout fileLayout;
    private List<FileEntity> fileList = Collections.synchronizedList(new ArrayList<FileEntity>());
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_comment_reply);
        ticketId = getIntent().getStringExtra("id");
        inflater = LayoutInflater.from(this);
        initView();
        initListener();
//        UIProvider.getInstance().addActivity(this);
    }

    private void initView() {
        ibBack = $(R.id.ib_back);
        btnSend = $(R.id.btn_send);
        editText = $(R.id.edittext);
        tvAddFile = $(R.id.tv_add_file);
        fileLayout = $(R.id.file_layout);
        recButton = $(R.id.btn_voice);
    }

    private void initListener() {
        ibBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        tvAddFile.setOnClickListener(this);
        recButton.setAudioFinishRecorderListener(new RecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                uploadFile(filePath);
            }
        });
    }


    private void setTagView(FileEntity entity){
        if (entity == null){return;}
        fileList.add(entity);
        notifyChanged();
    }

    private void delClick(View view, int position){
        if (position >= fileList.size()){
            return;
        }
        fileList.remove(position);
        notifyChanged();
    }

    private View animView;

    private void playVoiceItem(View v, String voiceLocalPath) {
        //播放动画
        if (animView != null) {
            animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
            animView = null;
        }

        animView = v.findViewById(R.id.id_recorder_anim);
        animView.setBackgroundResource(R.drawable.hd_voice_from_icon);
        AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
        anim.start();

        //播放音频
        MediaManager.playSound(getBaseContext(), voiceLocalPath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                animView.setBackgroundResource(R.drawable.hd_chatfrom_voice_playing);
            }
        });
    }

    public void notifyChanged(){
        fileLayout.removeAllViews();
        synchronized (fileList){
            for (int i = 0; i < fileList.size(); i++){
                FileEntity item = fileList.get(i);
                String type = item.type;
                final String localPath = item.localPath;
                final View view;
                if (type != null && type.equals("audio")) {
                    view = inflater.inflate(R.layout.em_comment_audio_with_delete, null);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(localPath)){
                                playVoiceItem(v, localPath);
                            }
                        }
                    });
                } else {
                    view = inflater.inflate(R.layout.em_comment_file_with_delete, null);
                    TextView tvName = (TextView) view.findViewById(R.id.tv_file_name);
                    tvName.setText(item.name);
                }
                ImageView ivDel = (ImageView) view.findViewById(R.id.delete);
                final int finalI = i;
                ivDel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        delClick(view, finalI);
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(this, 30));
                lp.topMargin = DensityUtil.dip2px(this, 5);
                lp.bottomMargin = DensityUtil.dip2px(this, 5);
                lp.leftMargin = DensityUtil.dip2px(this, 5);
                lp.rightMargin = DensityUtil.dip2px(this, 5);
                fileLayout.addView(view, lp);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_send:
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content) && fileList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "评论内容不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                createComment(ticketId, content);
                break;
            case R.id.tv_add_file:
                openFilesView();
                break;
        }
    }

    private void openFilesView() {

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            }
        }
    }


    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            uploadFile(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            uploadFile(file.getPath());
        }

    }


    public FileEntity getFileEntityByFilePath(File file) {
        String fileName = file.getName();
        FileEntity fileEntity = new FileEntity();
        fileEntity.name = fileName;
        String type = "file";
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (isImage(prefix)) {
            type = "img";
        }else if (isAudio(prefix)){
            type = "audio";
        }
        fileEntity.type = type;
        fileEntity.localPath = file.getPath();
        return fileEntity;
    }


    private boolean isImage(String prefix) {
        if (TextUtils.isEmpty(prefix)) {
            return false;
        }
        return prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                || prefix.equalsIgnoreCase("bmp") || prefix.equalsIgnoreCase("gif")
                || prefix.equalsIgnoreCase("jpeg") || prefix.equals("webp");
    }

    private boolean isAudio(String prefix){
        if (TextUtils.isEmpty(prefix)){
            return false;
        }
        return prefix.equalsIgnoreCase("amr") || prefix.equalsIgnoreCase("mp3");
    }

    private void uploadFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {

            return;
        }
        showDialog();
        // create upload service client
        FileUploadManager.FileUploadService service = FileUploadManager.retrofit().create(FileUploadManager.FileUploadService.class);

        // create RequestBody instance from file
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                closeDialog();
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string().trim();
                        JSONObject jsonObject = new JSONObject(result);
                        String uuid = jsonObject.getJSONArray("entities").getJSONObject(0).getString("uuid");
                        String remoteUrl = String.format("%1$schatfiles/%2$s", FileUploadManager.SERVER_URL, uuid);
                        FileEntity fileEntity = getFileEntityByFilePath(file);
                        fileEntity.remoteUrl = remoteUrl;
                        setTagView(fileEntity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Upload Error:" + t.getMessage());
                closeDialog();
                Toast.makeText(getApplicationContext(), "文件上传失败", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void createComment(final String ticketId, String content) {
        if (!ChatClient.getInstance().isLoggedInBefore()) {
            Toast.makeText(getApplicationContext(), "请先登录!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setCanceledOnTouchOutside(false);
        }
        pd.setMessage("请等待...");
        pd.show();
        String target = MyApplication.imService;
        String userId = ChatClient.getInstance().getCurrentUserName();
        NewCommentBody newCommentBody = new NewCommentBody();
        newCommentBody.setContent(content);
        NewCommentBody.CreatorBean creatorBean = new NewCommentBody.CreatorBean();
        creatorBean.setName(userId);
        creatorBean.setUsername(userId);
        creatorBean.setType("VISITOR"); // 此处必须是访客(VISITOR)
        newCommentBody.setCreator(creatorBean);
        String projectId = MyApplication.projectId;

        newCommentBody.setAttachments(getAttachements(fileList));
        Gson gson = new Gson();
        String newCommentBodyJson = gson.toJson(newCommentBody);
        TicketManager.getInstance().createComment(projectId, ticketId, target, newCommentBodyJson, new ValueCallBack<String>(){

            @Override
            public void onSuccess(String value) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Toast.makeText(getApplicationContext(), "评论成功!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDialog();
                        Toast.makeText(getApplicationContext(), "评论失败!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    private List<NewCommentBody.AttachmentsBean> getAttachements(List<FileEntity> fileEntities) {
        List<NewCommentBody.AttachmentsBean> beanList = new ArrayList<>();
        for (FileEntity fileEntity : fileEntities) {
            NewCommentBody.AttachmentsBean bean = new NewCommentBody.AttachmentsBean();
            bean.setType(fileEntity.type);
            bean.setName(fileEntity.name);
            bean.setUrl(fileEntity.remoteUrl);
            beanList.add(bean);
        }
        return beanList;
    }


    private void showDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
            pd.setMessage("文件上传中...");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
        }
        pd.show();
    }

    private void closeDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialog();
//        UIProvider.getInstance().removeActivity(this);
    }

    class FileEntity {
        String type;
        String name;
        String localPath;
        String remoteUrl;
    }


}
