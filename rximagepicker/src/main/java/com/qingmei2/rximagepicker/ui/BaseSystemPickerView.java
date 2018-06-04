package com.qingmei2.rximagepicker.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.qingmei2.rximagepicker.entity.Result;
import com.qingmei2.rximagepicker.function.Functions;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("CheckResult")
public abstract class BaseSystemPickerView extends Fragment {

    public static final int GALLERY_REQUEST_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;

    private PublishSubject<Boolean> attachedSubject = PublishSubject.create();

    private PublishSubject<Result> publishSubject;

    private PublishSubject<Integer> canceledSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachedSubject.onNext(true);
        attachedSubject.onComplete();
    }

    public Observable<Result> getUriObserver() {
        publishSubject = PublishSubject.create();
        canceledSubject = PublishSubject.create();

        requestPickImage();
        return publishSubject.takeUntil(canceledSubject);
    }

    private void requestPickImage() {
        if (!isAdded()) {
            attachedSubject.subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean attached) throws Exception {
                    startRequest();
                }
            });
        } else {
            startRequest();
        }
    }

    public abstract void startRequest();

    public abstract Uri getActivityResultUri(Intent data);

    private void onImagePicked(Uri uri) {
        if (publishSubject != null) {
            publishSubject.onNext(Functions.parseResultNoExtraData(uri));
            publishSubject.onComplete();
        }
        closure();
    }

    /**
     * When the pick image behavior ending, remove this fragment from the activity.
     */
    private void closure() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    protected boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRequest();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                case CAMERA_REQUEST_CODE:
                    onImagePicked(getActivityResultUri(data));
                    break;
            }
        } else {
            canceledSubject.onNext(requestCode);
        }
    }
}
