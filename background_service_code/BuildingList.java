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
	public String toString() {
		StringBuilder out = new StringBuilder(1000);
		
		for (Building building : this.buildings.values()) {
			out.append(building.toString() + "\n");
		}
		
		return (out.toString());
	}

}
