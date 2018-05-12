package all;
// queue implemented by circular array
// (not tested independently)

public class StringQueue {
	private String[] elements;
	private int maxSize, size, last, first;

	StringQueue(int size) {
		maxSize = size;
		elements = new String[size];
	}

	void add(String s) {
		if (size == maxSize) {
			size--;
			if (++first == maxSize)
				first = 0;
		}

		size++;
		elements[last++] = s;
		if (last == maxSize)
			last = 0;
	}

	String remove() {
		if (size == 0)
			return null;

		size--;
		String s = elements[first++];
		if (first == maxSize)
			first = 0;
		return s;
	}

	String[] removeAll() {
		String[] r = new String[size];
		for (int i = first; size > 0; i--)
			r[i] = remove();
		return r;
	}

}
