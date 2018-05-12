package list;

// double linked list, add to front, remove from back, generic queue

public class Queue<T> {
	Node head;
	int size;
	Node iter;

	// adds to front
	public void add(T t) {
		if (head != null) {
			head = new Node(t, head, head.prev);
			head.next.prev = head;
			head.prev.next = head;
		} else
			head = new Node(t);
		size++;
	}

	// removes from back (first in, first out)
	public T remove() {
		if (size == 0)
			return null;
		size--;
		T r = head.prev.t;
		if (size != 0) {
			head.prev = head.prev.prev;
			head.prev.next = head;
		} else
			head = null;
		return r;
	}

	T[] fillArray(T[] a) {
		if (a.length != size)
			System.out.println("Queue.fillArray size mismatch");
		else

			for (int i = 0; i < a.length; i++)
				a[i] = remove();

		return a; // for convenience
	}

	class Node {
		Node next, prev;
		T t;

		Node(T t, Node next, Node prev) {
			this.t = t;
			this.next = next;
			this.prev = prev;
		}

		// for adding first element
		public Node(T t) {
			this.t = t;
			next = this;
			prev = this;
		}
	}

}
