package com.example.deepak.myfbmessanger.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deepak.myfbmessanger.MyService;
import com.example.deepak.myfbmessanger.R;
import com.example.deepak.myfbmessanger.db.Data;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import java.util.ArrayList;

/**
 * Created by deepak on 30/5/15.
 */
public class MyAdapter extends ArrayAdapter<Data> {
    Context context;
    public static final int colored = Color.parseColor("#AE6118");
    public static final   int colorgreen = Color.parseColor("#21DA65");
    public MyAdapter(Context context, int resource, ArrayList<Data> datalist) {


        super(context, resource);
        this.context = context;
    }

    static class ViewHolder {
        public ViewHolder() {
            MyService.BUS.register(this);
        }


        @Subscribe
        public void onPresence(Presence presence) {
            if (presence.getFrom().equals(data.getId())) {

                data.setOnline(presence.isAvailable());
                MyService.userIdDataHashMap.get(data.getId()).setOnline(presence.isAvailable());
                if (presence.isAvailable()) {
                    imageView.setColorFilter(colorgreen);
                } else {
                    imageView.setColorFilter(colored);
                }
            }
        }
        public  int position;
        public TextView textView;
        public ImageView imageView;
        public ImageView imageFace;
        public Data data;
        //public  int position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.adaper_user_list, parent, false);

            ImageView imageface = (ImageView)view.findViewById(R.id.circle_facebookImage);
           // imageface.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_launcher));


            TextView textView = (TextView) view.findViewById(R.id.label);
            ImageView imageView = (ImageView) view.findViewById(R.id.circle_background);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.imageFace= imageface;
            viewHolder.textView = textView;
            viewHolder.imageView = imageView;
            viewHolder.position= position;
            Data s = getItem(position);
            viewHolder.data = s;

         /*   if(s.getImage() != null ){
                int len= s.getImage().length;
                Bitmap bitmap = BitmapFactory.decodeByteArray(s.getImage(), 0, len);
                if(bitmap != null){
                    viewHolder.imageFace.setImageBitmap(bitmap);
                }
            }*/


           MyDisplayAsynctask myDisplayAsynctask = new MyDisplayAsynctask(viewHolder);
            myDisplayAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            viewHolder.textView.setText(s.getUsername());


            if (s.isOnline()) {
                viewHolder.imageView.setColorFilter(colorgreen);
            } else {
                viewHolder.imageView.setColorFilter(colored);
            }

            view.setTag(viewHolder);
            return view;
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            Data s = getItem(position);
            viewHolder.position = position;
          /*  if(s.getImage() != null ){
                int len= s.getImage().length;
                Bitmap bitmap = BitmapFactory.decodeByteArray(s.getImage(), 0, len);
                if(bitmap != null){
                    viewHolder.imageFace.setImageBitmap(bitmap);
                }
            }*/


            MyDisplayAsynctask myDisplayAsynctask = new MyDisplayAsynctask(viewHolder);
            myDisplayAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            viewHolder.textView.setText(s.getUsername());
            viewHolder.data = s;


            if (s.isOnline()) {
                viewHolder.imageView.setColorFilter(colorgreen);
            } else {
                viewHolder.imageView.setColorFilter(colored);
            }

            return convertView;
        }


    }
    private class MyDisplayAsynctask extends AsyncTask<Void, Void, Bitmap> {
        private int pos;
        private ViewHolder viewHolder;

        public MyDisplayAsynctask(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
            this.pos = viewHolder.position;
        }

        @Override
        protected void onPreExecute() {
            viewHolder.imageFace.setImageDrawable(context.getResources().getDrawable(R.drawable.loading));

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;

            XMPPConnection connection = MyService.getConnection();
            ProviderManager.getInstance().addIQProvider("vCard","vcard-temp",new VCardProvider());
            VCard card = new VCard();
            try {
                card.load(connection, viewHolder.data.getId());
                byte[] imgs = card.getAvatar();
                if (imgs != null) {
                    int len = imgs.length;
                    bitmap = BitmapFactory.decodeByteArray(imgs, 0, len);
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            }


               return bitmap;

        }


        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (viewHolder.position == pos && result != null) {
                viewHolder.imageFace.setImageBitmap(result);
            }
        }
    }

}
