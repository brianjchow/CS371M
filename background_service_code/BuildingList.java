import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

final class BuildingList {

	private Map<String, Building> buildings;
	
	protected BuildingList() {
		this.buildings = new TreeMap<String, Building>();
	}

	protected boolean contains_building(String name) {
		if (this.get_building(name) == null) {
			return false;
		}
		return true;
	}
		
	protected Building get_building(String name) {
		if (name == null || name.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		return (this.buildings.get(name.toUpperCase()));
	}
	
	protected Iterator<Map.Entry<String, Building>> get_iterator() {
		return this.buildings.entrySet().iterator();
	}
	
	protected int get_size() {
		return this.buildings.size();
	}

	protected boolean put_building(String name, Building building) {
		if (name == null || name.length() <= 0 || building == null) {
//			return false;
			throw new IllegalArgumentException();
		}
		
		this.buildings.put(name, building);
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if (!(other instanceof BuildingList)) {
			return false;
		}
		
		BuildingList other_list = (BuildingList) other;
		
		if (this.get_size() != other_list.get_size()) {
			return false;
		}
		
		String curr_bldg_str;
		Building curr_bldg, other_bldg;
		for (Map.Entry<String, Building> entry : other_list.buildings.entrySet()) {
			curr_bldg_str = entry.getKey();
			curr_bldg = entry.getValue();
			
			if ((other_bldg = this.buildings.get(curr_bldg_str)) == null) {
				return false;
			}
			
			if (!other_bldg.equals(curr_bldg)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(1000);
		
		for (Building building : this.buildings.values()) {
			out.append(building.toString() + "\n");
		}
		
		return (out.toString());
	}

}
