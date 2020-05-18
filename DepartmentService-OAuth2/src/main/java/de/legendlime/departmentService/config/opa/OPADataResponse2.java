package de.legendlime.departmentService.config.opa;

import java.util.ArrayList;
import java.util.List;

public class OPADataResponse2 {

    private List<OPARole> opaRole = new ArrayList<OPARole>();;

    public OPADataResponse2() {
    }

	public List<OPARole> getOpaRole() {
		return opaRole;
	}

	public void setOpaRole(List<OPARole> opaRole) {
		this.opaRole = opaRole;
	}

	@Override
	public String toString() {
		return "[authority=" + opaRole + "]";
	}

}
