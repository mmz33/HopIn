package aub.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

public class MapHistorySettings extends AppCompatActivity {

    private ListView historyList;

    private TextView makeItem(String date, String time, String location) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText(date + " ; " + time + " ; " + location);
        tv.setTextSize(15);
        return tv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        historyList = (ListView)findViewById(R.id.map_history_list);
        //historyList.removeAllViews();

        // Add a 'No data' view to the list.
        TextView noData = new TextView(getApplicationContext());
        noData.setText("No data.");
        noData.setTextSize(15);
        historyList.addView(noData);

        // Dispatch thread to load map history from server.
        Thread handler = new Thread(
            new Runnable() {
                public void run() {
                    //ServerRequest request = Server.queryMapHistory(UserSession.getActiveSession());
                    //while (request.status.get() == ServerRequestStatus.Pending.ordinal()) {
                    //    try { wait(32); } catch (Exception e) {}
                    //}
                    //historyList.removeAllViews();
                    //String[] data = (String[])request.response;
                    //for (int i = 0; i < data.length; ++i) {
                    //    historyList.addView(makeItem(data[3*i], data[3*i + 1], data[3*i + 2]));
                    //}
                }
            });
        handler.start();
    }

}

