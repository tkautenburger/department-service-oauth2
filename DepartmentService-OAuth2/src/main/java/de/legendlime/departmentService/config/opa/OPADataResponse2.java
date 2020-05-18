package de.legendlime.departmentService.config.opa;

import java.util.ArrayList;
import java.util.List;

public class OPADataResponse2 {

    private List<OPARole> result = new ArrayList<OPARole>();;

    public OPADataResponse2() {
    }

	public List<OPARole> getResult() {
		return result;
	}

	public void setResult(List<OPARole> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "[authority=" + result + "]";
	}

}
