package math;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer
 * with any number of digits, which overcomes the computer storage length
 * limitation of an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;

	/**
	 * Number of digits in this integer
	 */
	int numDigits;

	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST
	 * node. For instance, the integer 235 would be stored as: 5 --> 3 --> 2
	 */
	DigitNode front;

	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}

	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first
	 * character (no sign means positive), and at least one digit character
	 * (including zero). Examples of correct format, with corresponding values
	 * Format Value +0 0 -0 0 +123 123 1023 1023 0012 12 0 0 -123 -123 -001 -1
	 * +000 0
	 * 
	 * 
	 * @param integer
	 *            Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer
	 * @throws IllegalArgumentException
	 *             If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) throws IllegalArgumentException {

		// Pass arg into variable so we can operate it
		String str = integer;

		// Create BigInteger to be returned
		BigInteger temp = new BigInteger();

		// Ignore hanging zeros until we reach a significant value
		boolean checkLeadingZero = true;

		// This will hold the value to add
		int toAdd = 0;

		// Trim spaces before and after string
		str = str.trim();
		System.out.println("String is now::" + str);
		// Check for sign in front of String, set negative to proper value
		if (str.substring(0, 1).equals("-")) {
			temp.negative = true;
			str = str.substring(1);
		} else if (str.substring(0, 1).equals("+")) {
			str = str.substring(1);
		}
		System.out.println("String is now::" + str);

		// Iterate through String
		for (int i = 0; i < str.length(); i++) {

			// Check if current character is a digit
			if (!Character.isDigit(str.charAt(i))) {
				throw new IllegalArgumentException();
			}

			// Checks to see if there are leading zeros to ignore.
			// After first non-zero digit, no longer need to check
			toAdd = Integer.parseInt(str.substring(i, i + 1));
			if (checkLeadingZero && toAdd == 0) {
				continue;
			} else {
				checkLeadingZero = false;
			}
			System.out.println("Adding new DigitNode with value " + str.charAt(i)); // DEBUG

			// Add DigitNode as needed and increment numDigits
			temp.front = new DigitNode(toAdd, temp.front);
			temp.numDigits++;
		}

		System.out.println(
				"Returning BigInteger with numDigits: " + temp.numDigits + " and with negative: " + temp.negative); // DEBUG
		return temp;
	}

	/**
	 * Adds an integer to this integer, and returns the result in a NEW
	 * BigInteger object. DOES NOT MODIFY this integer. NOTE that either or both
	 * of the integers involved could be negative. (Which means this method can
	 * effectively subtract as well.)
	 * 
	 * @param other
	 *            Other integer to be added to this integer
	 * @return Result integer
	 */
	public BigInteger add(BigInteger other) {

		// Initialize return variable
		BigInteger temp = new BigInteger();

		DigitNode ptrA = front;
		DigitNode ptrB = other.front;
		int digitA = 0;
		int digitB = 0;
		int sum = 0;
		int carry = 0;

		// Case 1: Same sign
		if (negative == other.negative) {
			temp.negative = negative;
			while (ptrA != null || ptrB != null) {
				// Check if one number is longer than other, if so, assign 0 as
				// placeholder for addition, i.e. "changing" so they are the
				// same length
				if (ptrA == null)
					digitA = 0;
				else
					digitA = ptrA.digit;

				if (ptrB == null)
					digitB = 0;
				else
					digitB = ptrB.digit;

				// Perform addition, remember to carry for digits <= 10
				sum = digitA + digitB + carry;
				if (sum >= 10) {
					sum -= 10;
					carry = 1;
				} else // Remember to reset carry if not greater than 10
					carry = 0;

				System.out.println("Evaluated a sum of " + sum); // DEBUG
				temp.addToEnd(sum);
				numDigits++;

				// DO NOT TRAVERSE IF NULL
				if (ptrA != null)
					ptrA = ptrA.next;
				if (ptrB != null)
					ptrB = ptrB.next;
			}
			// Remember to execute final carry if necessary, will only occur if
			// hanging carry
			if (carry == 1) {
				temp.addToEnd(1);
				numDigits++;
			}
		}

		if (negative != other.negative) { // Case 2: Opposite signs, perform
											// subtraction
			boolean borrow = false;
			while (ptrA != null || ptrB != null) {
				// Check if one number is longer than other, if so, assign 0 as
				// placeholder for addition
				if (ptrA == null)
					digitA = 0;
				else
					digitA = ptrA.digit;
				if (ptrB == null)
					digitB = 0;
				else
					digitB = ptrB.digit;
				if (borrow)
					digitA--;

				if (digitA < digitB) { // Need to borrow from next greatest
										// position
					sum = ((digitA) + 10) - digitB;
					borrow = true;
				} else {
					sum = (digitA) - digitB;
					borrow = false;
				}
				System.out.println("Evaluated " + sum); // DEBUG
				temp.addToEnd(sum);
				temp.numDigits++;

				// DO NOT TRAVERSE IF NULL
				if (ptrA != null)
					ptrA = ptrA.next;
				if (ptrB != null)
					ptrB = ptrB.next;
			}
			temp.negative = negative;
			if (borrow) { // If borrowing from non-existent node, final result
							// will have !negative
				int mark = 0;
				System.out.println("Result is negative, operating complement"); // DEBUG
				temp.negative = !negative;
				DigitNode ptrNeg = temp.front;
				// This also means that the final result is actually incorrect
				// Operate and find complement to determine correct result
				for (int i = 0; i < temp.numDigits; i++) {
					if (i == mark) { // If least significant digit is 0, push
										// complement mark by one place
						if (ptrNeg.digit == 0)
							mark++;
						else
							ptrNeg.digit = 10 - ptrNeg.digit;
					} else
						ptrNeg.digit = 9 - ptrNeg.digit;
					System.out.println("Changing to " + ptrNeg.digit);
					ptrNeg = ptrNeg.next;
				}
			}
		}
		temp.clean();
		/*
		 * Ask Prof Centeno about negative 0 sums if(temp.front.digit == 0 &&
		 * temp.front.next == null) { temp.negative = false; }
		 */
		System.out.println("Returning a result of " + temp.toString()); // DEBUG
		temp.numDigits = temp.count();
		return temp;
	}

	/**
	 * Returns the BigInteger obtained by multiplying the given BigInteger with
	 * this BigInteger - DOES NOT MODIFY this BigInteger
	 * 
	 * @param other
	 *            BigInteger to be multiplied
	 * @return A new BigInteger which is the product of this BigInteger and
	 *         other.
	 */
	public BigInteger multiply(BigInteger other) {

		BigInteger total = new BigInteger(); // Variable to be returned
		BigInteger curr = new BigInteger(); // Will store current multiplicative
											// value to then add
		int carry = 0;
		int sum = 0;
		int place = 0;
		total.front = new DigitNode(0, total.front); // So you can actually add
														// the numbers

		for (DigitNode ptrA = front; ptrA != null; ptrA = ptrA.next) {
			for (DigitNode ptrB = other.front; ptrB != null; ptrB = ptrB.next) {
				sum = (ptrA.digit * ptrB.digit) + carry;
				if (sum >= 10) { // Perform multiplicative carry if necessary
					carry = sum / 10; // Take tens value to carry
					sum = sum % 10; // Add only the ones value of resultant
				} else {
					carry = 0; // Remember to reset carry
				}
				System.out.println("Adding DigitNode to curr with value of " + sum); // DEBUG
				curr.addToEnd(sum);
			}
			if (carry > 0) {
				curr.addToEnd(carry); // Same concept as addition, you have to
										// take care of hanging carry if exists
			}
			curr.shift(place); // Perform shift for aggregate
			System.out.println("curr has " + curr.toString()); // DEBUG
			System.out.println("total has " + total.toString()); // DEBUG
			place++; // Increment shift in order to properly aggregate each
						// iteration
			total = total.add(curr); // Aggregate total
			curr.front = null;
			System.out.println("Aggregate is now " + total.toString()); // DEBUG
			carry = 0; // RESET CARRY AFTER EACH CURR OPERATION
		}

		// Determine negative for final result
		if (negative == other.negative)
			total.negative = false;
		else
			total.negative = true;
		total.numDigits = total.count();
		return total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}

		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
			retval = curr.digit + retval;
		}

		if (negative) {
			retval = '-' + retval;
		}

		return retval;
	}

	// Helper method to add a node at the end of the BigInteger
	private void addToEnd(int data) {
		boolean done = false;
		DigitNode ptr = front;
		if (front == null) {
			front = new DigitNode(data, front);
			done = true;
		}
		while (!done) {
			if (ptr.next == null) {
				ptr.next = new DigitNode(data, null);
				done = true;
			} else
				ptr = ptr.next;
		}
	}

	// Helper method to clean BigInteger of insignificant zeros
	private void clean() {
		System.out.println("Running clean!"); // DEBUG
		DigitNode curr = front;
		DigitNode prev = front;
		while (curr != null) {
			if(curr.digit != 0) {
				prev = curr;
			}
			curr = curr.next;
		}
		if (prev != null)
			prev.next = null;
	}

	// Helper method to shift number by one place, to be used for aggregating
	// sum in multiply method
	private void shift(int shiftNum) {
		System.out.println("Performing " + shiftNum + " shifts.");
		for (int i = 0; i < shiftNum; i++) {
			front = new DigitNode(0, front);
		}
	}
	
	private int count() {
		int count = 0;
		for(DigitNode ptr = front; ptr != null; ptr = ptr.next) {
			count++;
		}
		return count;
	}
}
