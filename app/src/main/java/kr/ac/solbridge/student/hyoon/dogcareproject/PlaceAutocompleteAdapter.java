package kr.ac.solbridge.student.hyoon.dogcareproject;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.PendingResult;

import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.AutocompleteFilter;

import com.google.android.gms.location.places.AutocompletePrediction;

import com.google.android.gms.location.places.AutocompletePredictionBuffer;

import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable{


    Context context;
    PlaceAutoCompleteInterface listener;
    private static final String TAG ="PlaceAutoCompleteAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    ArrayList<PlaceAutocomplete> resultList;
    private GoogleApiClient googleApiClient;
    private LatLngBounds bounds;
    private  int layout;
    private AutocompleteFilter placeFilter;

    public interface PlaceAutoCompleteInterface{
        public void OnPlaceClick(ArrayList<PlaceAutocomplete> mResultList,int position);
    }
    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View convertView = layoutInflater.inflate(this.layout, viewGroup, false);

        PlaceViewHolder mPredictionHolder = new PlaceViewHolder(convertView);

        return mPredictionHolder;




    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder mPredictionHolder, final int i) {
        mPredictionHolder.address.setText(resultList.get(i).description);

        mPredictionHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnPlaceClick(resultList, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (resultList != null)
            return resultList.size();
        else
            return 0;
    }
    public PlaceAutocomplete getitem(int position)
    {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter()  {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();



                // 제약 조건이 주어지지 않으면 자동 완성 쿼리를 건너 뜁니다.

                if (constraint != null) {

                    //(제약) 검색 문자열에 대한 자동 완성 API를 쿼리하십시오.

                    resultList = getAutocomplete(constraint);

                    if (resultList != null) {
                        //API가 성공적으로 결과를 반환했습니다.

                        results.values = resultList;
                        results.count = resultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {


                    //API가 하나 이상의 결과를 반환하고 데이터를 업데이트합니다.

                    notifyDataSetChanged();

                } else {

                    //API가 결과를 반환하지 않았고 데이터 세트를 무효화
                    //notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
    public void clearList()
    {
        if (resultList != null && resultList.size() > 0) {

            resultList.clear();

        }
    }




    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }


    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {

        if (googleApiClient.isConnected()) {

            // 자동 완성 API에 쿼리를 제출하고 PendingResult를 검색합니다.

            // 쿼리가 완료되면 결과를 포함합니다.

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(googleApiClient, constraint.toString(),

                                    bounds, placeFilter);
            // 이 메소드는 기본 UI 스레드에서 호출되어야합니다. API의 결과를 차단하고 최대 60 초 동안 기다립니다.

            AutocompletePredictionBuffer autocompletePredictions = results



                    .await(60, TimeUnit.SECONDS);



            // 쿼리가 성공적으로 완료되었는지 확인하고, 그렇지 않으면 null을 반환합니다.

            final Status status = autocompletePredictions.getStatus();


            if (!status.isSuccess()) {
                Log.e("", "Error getting autocomplete prediction API call: " + status.toString());

                autocompletePredictions.release();

                return null;

            }

            Log.i("", "Query completed. Received " + autocompletePredictions.getCount()

                    + " predictions.");

            // 버퍼를 고정 할 수 없으므로 결과를 자체 데이터 구조에 복사합니다.

            // AutocompletePrediction 객체는 API 응답 (장소 ID 및 설명)을 캡슐화합니다.

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();

            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());

            while (iterator.hasNext()) {

                AutocompletePrediction prediction = iterator.next();

                // 세부 정보를 가져 와서 새로운 PlaceAutocomplete 객체로 복사합니다.

                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),

                        prediction.getFullText(null)));
            }

            // 모든 데이터가 복사되었으므로 버퍼를 해제

            autocompletePredictions.release();

            return resultList;
        }
        Log.e("", "Google API client is not connected for autocomplete query.");

        return null;

    }




    public PlaceAutocompleteAdapter(Context context, int resourse, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter)
{
    this.context =context;
    this.layout = resourse;
    this.googleApiClient = googleApiClient;
    this.bounds = bounds;
    this.placeFilter = filter;
    this.listener = (PlaceAutoCompleteInterface)this.context;

}


    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout parentLayout;
        public TextView address;
        public PlaceViewHolder(View itemView) {
            super(itemView);

            parentLayout = (LinearLayout) itemView.findViewById(R.id.predictedRow);
            address = (TextView) itemView.findViewById(R.id.tv_search);
        }

    }


    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();

        }
    }


}

