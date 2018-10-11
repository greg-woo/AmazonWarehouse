
/*
Created by: GREG WOO
Program: Implementing Amazon warehouse automation algorithms
*/

public class Warehouse{

	protected Shelf[] storage;
	protected int nbShelves;
	public Box toShip;
	public UrgentBox toShipUrgently;
	static String problem = "problem encountered while performing the operation";
	static String noProblem = "operation was successfully carried out";

	public Warehouse(int n, int[] heights, int[] lengths){
		this.nbShelves = n;
		this.storage = new Shelf[n];
		for (int i = 0; i < n; i++){
			this.storage[i]= new Shelf(heights[i], lengths[i]);
		}
		this.toShip = null;
		this.toShipUrgently = null;
	}

	public String printShipping(){
		Box b = toShip;
		String result = "not urgent : ";
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n" + "should be already gone : ";
		b = toShipUrgently;
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n";
		return result;
	}

	public String print(){
		String result = "";
		for (int i = 0; i < nbShelves; i++){
			result += i + "-th shelf " + storage[i].print();
		}
		return result;
	}

	public void clear(){
		toShip = null;
		toShipUrgently = null;
		for (int i = 0; i < nbShelves ; i++){
			storage[i].clear();
		}
	}

	/**
	* initiate the merge sort algorithm
	*/
	public void sort(){
		mergeSort(0, nbShelves -1);
	}

	/**
	* performs the induction step of the merge sort algorithm
	* @param start
	* @param end
	*/
	protected void mergeSort(int start, int end){

		int mid = 0;
		if(start < end) {

			mid = (start + end) / 2;
			mergeSort(start, mid);
			mergeSort(mid + 1, end);
			merge(start, mid, end);

		}
	}

	/**
	* performs the merge part of the merge sort algorithm
	* @param start
	* @param mid
	* @param end
	*/
	protected void merge(int start, int mid, int end){

		int left = start;
		int right = end;
		int temp1 = mid + 1;
		int temp2 = left;

		Shelf[] tempArray = new Shelf[this.storage.length];

		while(left <= mid && temp1 <= right) {
			if(this.storage[left].height <= this.storage[temp1].height) {
				tempArray[temp2++] = this.storage[left++];
			}
			else {
				tempArray[temp2++] = this.storage[temp1++];
			}
		}

		while(left <= mid)
		tempArray[temp2++] = this.storage[left++];

		while(temp1 <= right) {
			tempArray[temp2++] = this.storage[temp1++];
		}

		for(int i = start; i <= end; i++) {
			this.storage[i] = tempArray[i];
		}


	}

	/**
	* Adds a box is the smallest possible shelf where there is room available.
	* Here we assume that there is at least one shelf (i.e. nbShelves >0)
	* @param b
	* @return problem or noProblem
	*/
	public String addBox (Box b){

		int heightBox = b.height;
		int counter = -1;
		int smallestHeight = 1000;

		for(int i = 0; i < nbShelves; i ++) {

			if(storage[i].height >= heightBox) {

				//We want to have the shelf with the smallest acceptable height
				if ((storage[i].height < smallestHeight) && (b.length <= storage[i].availableLength) ) {

					counter = i;
					smallestHeight = storage[i].height;
				}
			}
		}

		// if the counter hasn't changed
		if (counter  == -1) {
			return problem;

		} else {
			// check that the box can fit
			if((b.length > storage[counter].availableLength) || (b.height > storage[counter].height)) {

				return problem;
			} else {


				storage[counter].addBox(b);
				return noProblem;
			}

		}

	}

	/**
	* Adds a box to its corresponding shipping list and updates all the fields
	* @param b
	* @return problem or noProblem
	*/
	public String addToShip (Box b){

		if ( b instanceof UrgentBox ) {

			if(toShipUrgently == null) {
				toShipUrgently = (UrgentBox) b;

			} else {

				toShipUrgently.previous = (UrgentBox) b;
				b.next = toShipUrgently;
				toShipUrgently = (UrgentBox) b;
				toShipUrgently.previous = null;

			}

			return noProblem;

		} else {

			if(toShip == null) {
				toShip = b;

			} else {

				toShip.previous = b;
				b.next = toShip;
				toShip = b;
				toShip.previous = null;

			}

			return noProblem;

		}
	}

	/**
	* Find a box with the identifier (if it exists)
	* Remove the box from its corresponding shelf
	* Add it to its corresponding shipping list
	* @param identifier
	* @return problem or noProblem
	*/
	public String shipBox (String identifier){

		Box shipping = null;

		for (Shelf currentShelf : this.storage) {

			shipping = currentShelf.removeBox(identifier);

			if(shipping != null) {

				addToShip(shipping);
				return noProblem;

			}

		}

		return problem;
	}

	/**
	* if there is a better shelf for the box, moves the box to the optimal shelf.
	* If there are none, do not do anything
	* @param b
	* @param position
	*/
	public void moveOneBox (Box b, int position){

		int heightShelf = storage[position].height;
		int counter = -1;

		for (int i = 0; i < nbShelves; i ++) {
			//check if the shelf is not the same shelf as position
			// and that the shelf has a smaller height than the current shelf
			if ((i != position) && (storage[i].height < heightShelf) ) {

				if((b.length <= storage[i].availableLength) && (b.height <= storage[i].height)) {

					counter = i;
					break;

				}
			}

		}

		if (counter  != -1) {
			storage[position].removeBox(b.id);
			storage[counter].addBox(b);
		}
	}

	/**
	* reorganize the entire warehouse : start with smaller shelves and first box on each shelf.
	*/
	public void reorganize (){

		for (int i = 0; i < nbShelves; i ++) {

			Box b = storage[i].firstBox;
			Box temp;
			while(b != null){
				temp = b.next;
				moveOneBox(b,i);
				b = temp;
			}
		}
	}
}
