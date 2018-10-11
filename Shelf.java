
/*
Created by: GREG WOO
Program: Implementing Amazon warehouse automation algorithms
*/

public class Shelf {

	protected int height;
	protected int availableLength;
	protected int totalLength;
	protected Box firstBox;
	protected Box lastBox;

	public Shelf(int height, int totalLength){
		this.height = height;
		this.availableLength = totalLength;
		this.totalLength = totalLength;
		this.firstBox = null;
		this.lastBox = null;
	}

	protected void clear(){
		availableLength = totalLength;
		firstBox = null;
		lastBox = null;
	}

	public String print(){
		String result = "( " + height + " - " + availableLength + " ) : ";
		Box b = firstBox;
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n";
		return result;
	}

	/**
	* Adds a box on the shelf. Here we assume that the box fits in height and length on the shelf.
	* @param b
	*/
	public void addBox(Box b){


		// Reset the next and previous links
		b.next = b.previous = null;

		// Case if the shelf is empty
		if ((this.lastBox == null) || (this.firstBox == null)) {



			this.firstBox = b;
			this.lastBox = b;

		} else {

			// We add the box at the end of the shelf
			Box tempBox;
			this.lastBox.next = b;
			tempBox = this.lastBox;
			this.lastBox = b;
			this.lastBox.previous = tempBox;
		}

		// We update the available length
		this.availableLength -= b.length;
	}

	/**
	* If the box with the identifier is on the shelf, remove the box from the shelf and return that box.
	* If not, do not do anything to the Shelf and return null.
	* @param identifier
	* @return
	*/
	public Box removeBox(String identifier){

		Box b = firstBox;

		// While loop to check all possibilities
		while(b != null){

			if(b.id.equals(identifier)) {

				if (firstBox == b && b != lastBox) {
					b.next.previous = null;
					firstBox = b.next;

				} else if( b == lastBox && b != firstBox) {
					b.previous.next = null;
					lastBox = b.previous;
				} else if ( b != firstBox && b != lastBox) {

					b.next.previous = b.previous;
					b.previous.next = b.next;

				} else {
					this.firstBox = null;
					this.lastBox = null;

				}
				b.next = b.previous = null;
				this.availableLength = this.availableLength + b.length;

				return b;

			}

			b = b.next;

		}
		return null;

	}

}
