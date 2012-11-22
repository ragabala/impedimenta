package sorts;

/**
 * A class for testing sorts.  This is an abstract class that contains some
 * basic functionality related to randomly filling test arrays. To use, create a
 * subclass of this class which implements a sort() method that actually
 * performs the desired sort.
 *
 * @author ldamon
 *
 */
public abstract class Sort {
    int[] _array;

    /**
     * create a Sort, generating an array with a default 1000 elements
     */
    public Sort() {
        _array = generateArray(1000);
    }

    /**
     * create a Sort, generating an randome array with numElements elements
     * @param numElements the number of elements in the array to be sorted
     */
    public Sort(int numElements) {
        _array = generateArray(numElements);
    }

    /**
     * create a Sort, which can be used to sort the passed in array
     * @param array the array to sort
     */
    public Sort(int[] array) {
        _array = array;
    }

    /**
     * perform the sort
     */
    public abstract void sort();

    /**
     * generate an array with numElements random ints between 1 and 1000
     * @param numElements number of elements to put in the array
     * @return the generated array
     */
    private int[] generateArray(int numElements) {
        // Allocate our test array and fill it with random data.
        int[] arr = new int[numElements];
        for(int i = 0; i < numElements; ++i)
            arr[i] = (int)(Math.random()*1000) + 1;
        return arr;
    }

    /**
     * Returns the array being sorted.
     */
    public int[] getArray() {
        return this._array;
    }

    public String toString() {
        if(0 == _array.length) {
            return "";
        } else {
            String str = "" + _array[0];
            for(int i = 1; i < _array.length; i++) {
                str += ", " + _array[i];
            }
            return str;
        }
    }
}
