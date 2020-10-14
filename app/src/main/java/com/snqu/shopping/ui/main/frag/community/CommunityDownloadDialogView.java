package com.snqu.shopping.ui.main.frag.community;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.snqu.shopping.App;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.ui.goods.fragment.ShareFragment;
import com.snqu.shopping.util.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.widget.dialog.DialogView;

/**
 * 下载进度框
 *
 * @author 张全
 */
public class CommunityDownloadDialogView extends DialogView {
    private CommunityEntity communityEntity;
    private int total;
    private int progress;
    TextView item_label2;
    TextView btn;
    private List<File> files = new ArrayList<>();
    private FileDownloader fileDownloader;
    private ProgressBar progressBar;
    private String title;
    ExecutorService service = Executors.newCachedThreadPool();

    public CommunityDownloadDialogView(Context ctx, CommunityEntity communityEntity) {
        super(ctx);
        this.fileDownloader = new FileDownloader(ctx);
        this.communityEntity = communityEntity;
    }

    public CommunityDownloadDialogView(Context ctx, CommunityEntity communityEntity, String title) {
        super(ctx);
        this.fileDownloader = new FileDownloader(ctx);
        this.communityEntity = communityEntity;
        this.title = title;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.community_download_dialog;
    }

    @Override
    public void initView(View view) {

        item_label2 = findViewById(R.id.item_label2);
        progressBar = findViewById(R.id.progressBar);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(btn.getText().toString(), "下载失败")) { //重新下载
                    startDownload();
                } else {
                    fileDownloader.stop();
                    dismiss();
                }
            }
        });

        if (!TextUtils.isEmpty(title) && !"sharegoods".equals(title)) {
            TextView textView = findViewById(R.id.item_label1);
            textView.setText(title);
        }

        startDownload();
    }

    private void startDownload() {
        progress = 0;
        files.clear();
//        item_label2.setText("开始下载");
        btn.setText("取消下载");


        List<String> images = communityEntity.getItemList();
        total = images.size();
        progressBar.setMax(images.size());
        progressBar.setProgress(progress);
        item_label2.setText("已下载 " + progress + "/" + total);

        fileDownloader.downloadFile(images, new FileDownloader.DownloadCallback() {
            @Override
            public void success(File file, String url) {
                files.add(file);
                updateUI(file, url);
            }

            @Override
            public void fail(String url) {
                updateUI(null, url);
            }
        });
    }

    private void updateUI(File file, String url) {
        progress++;
        progressBar.setProgress(progress);
        item_label2.setText("已下载 " + progress + "/" + total);
        if (progress == total) {
            if (files.isEmpty()) {
                btn.setText("下载失败");
            } else {
                btn.setText("下载完成");
            }
        }
        if (TextUtils.isEmpty(title)) {
            service.execute(() -> {
                if (file != null && file.exists()) {
                    String name = Constant.water_name;
                    if (!TextUtils.isEmpty(name)) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath()).copy(Bitmap.Config.ARGB_8888, true);
                        if (bitmap != null) {
                            Bitmap newBitmap = ShareFragment.setWaterMark(App.mApp.getResources(), bitmap, name);
                            String path = file.getPath();
                            if (newBitmap != null) {
                                file.delete();
                                ImageUtils.save(newBitmap, new File(path), Bitmap.CompressFormat.PNG);
                            }
                        }
                    }
                }
            });
        }
    }
}
