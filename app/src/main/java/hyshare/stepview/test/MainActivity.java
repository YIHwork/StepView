package hyshare.stepview.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import hyshare.stepview.MetricsUtil;
import hyshare.stepview.R;
import hyshare.stepview.StepView;

public class MainActivity extends AppCompatActivity
{
  private ListView listView;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView = findViewById(R.id.list_item);
    listView.setAdapter(new MyAdapter());
  }

  class MyAdapter extends BaseAdapter
  {
    @Override
    public int getCount() {
      return 50;
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = getLayoutInflater().inflate(R.layout.step_view_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.stepView = convertView.findViewById(R.id.step_view);
        holder.stepTitle = convertView.findViewById(R.id.step_title);
        convertView.setTag(holder);
      }
      ViewHolder holder = (ViewHolder) convertView.getTag();
      if (position == 0) {
        holder.stepView.setLine1Visible(false);
        holder.stepView.setLine2Visible(true);
        holder.stepView.setStepNodeSize((int) (MetricsUtil.getDensity(MainActivity.this) * 20));
        holder.stepView.setStepNodeResource(android.R.drawable.ic_menu_add);
      } else {
        if (position == getCount() - 1) {
          holder.stepView.setLine1Visible(true);
          holder.stepView.setLine2Visible(false);
        } else {
          holder.stepView.setLine1Visible(true);
          holder.stepView.setLine2Visible(true);
        }
        holder.stepView.setStepNodeSize((int) (MetricsUtil.getDensity(MainActivity.this) * 7));
        holder.stepView.setStepNodeColor(getResources().getColor(R.color.colorAccent));
      }
      holder.stepView.setStepLineResource(R.color.colorAccent);
      return convertView;
    }

    class ViewHolder {
      TextView stepTitle;
      StepView stepView;
    }
  }
}
