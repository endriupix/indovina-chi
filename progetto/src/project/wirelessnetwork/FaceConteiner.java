package project.wirelessnetwork;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;

public class FaceConteiner extends ImageView implements Checkable {

	private String faceName;
	private boolean choosable = true;
	private boolean checked = false;

	public FaceConteiner(String name, Context context) {
		super(context);
		faceName = name;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;

	}

	public void toggle() {
		checked = !checked;
		View parent = (View) getParent();
		if (checked) {
			parent.setBackgroundResource(R.color.image_selected);
		}
		else {
			parent.setBackgroundResource(R.color.image_not_selected);
		}

	}
	
	public boolean isChoosable() {
		return choosable;
	}
	
	public void noMoreChoosable() {
		choosable = false;
		((View)getParent()).setBackgroundResource(R.color.image_not_selected);
	}
		

}
