package org.raisercostin.util;

import java.util.*;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

public class Collections3 {

	public static <T> List<T> minus(List<T> list1, List<T> list2) {
		if (list1 == null) {
			return Lists.<T> newArrayList();
		}
		ArrayList<T> result = Lists.newArrayList(list1);
		if (list2 != null) {
			result.removeAll(list2);
		}
		return result;
	}

	public static <T> List<T> union(T[] list1, T... list2) {
		return union(Lists.newArrayList(list1), Lists.newArrayList(list2));
	}

	public static <T> List<T> union(List<T> list1, T... list2) {
		return union(list1, Lists.newArrayList(list2));
	}

	//
	// public static <T> List<T> union(List<T> participants, T...
	// counterparties) {
	// List<T> result = participants == null ? new ArrayList<T>()
	// : new ArrayList<>(participants);
	// result.addAll(Lists.newArrayList(counterparties));
	// return result;
	// }

	public static <T> List<T> union(List<T> list1, List<T> list2) {
		// CTC adding || list1.size() == 0, This offers some protection to the
		// original sorting of the list, which
		// otherwise causes
		// artificial and un-needed changes when FlowState object is read into a
		// FlowState2 object. This does not solve
		// but partially the problem
		if (list1 == null || list1.size() == 0) {
			if (list2 == null) {
				return Lists.newArrayList();
			}
			return list2;
		} else {
			if (list2 == null || list2.size() == 0) {
				return list1;
			}
		}
		Set<T> result = new TreeSet<T>(list1);
		result.addAll(list2);
		return Lists.newArrayList(result);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> union(List<T> list1, T element) {
		Preconditions.checkNotNull(element);
		if (list1 == null) {
			return Lists.newArrayList(element);
		}
		Set<T> result = new TreeSet<T>(list1);
		result.add(element);
		return Lists.newArrayList(result);
	}

	public static <T> List<T> minus(List<T> newParticipants, T address) {
		return minus(newParticipants, Lists.newArrayList(address));
	}

	public static <T extends Comparable<T>> Comparator<T> comparator() {
		return new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return o1.compareTo(o2);
			}
		};
	}

	public static <T> void unionInPlace(List<T> list1, List<T> list2) {
		for (T element : list2) {
			if (!list1.contains(element)) {
				list1.add(element);
			}
		}
	}

	public static <T> List<T> updateList(List<T> list, List<T> toAdd, List<T> toRemove) {
		return minus(union(list, toAdd), toRemove);
	}

	public static <T extends Comparable<T>> List<T> distinct(List<T> list) {
		return Lists.newArrayList(Sets.newTreeSet(list));
	}

	public static <T extends Comparable<T>> List<T> sortedDistinct(T... list) {
		return Ordering.natural().sortedCopy(ImmutableSet.copyOf(list).asList());
	}

	public static <T extends Comparable<T>> List<T> sortedDistinct(List<T> list) {
		return Ordering.natural().sortedCopy(ImmutableSet.copyOf(list).asList());
	}

	public static <T extends Comparable<T>> List<T> sort(Optional<List<T>> list) {
		if (list.isPresent()) {
			return Ordering.natural().sortedCopy(list.get());
		} else {
			return Lists.newArrayList();
		}
	}

	public static <T extends Comparable<T>> List<T> sort(T[] list) {
		if (list == null) {
			return Lists.newArrayList();
		}
		return Ordering.natural().sortedCopy(ImmutableSet.copyOf(list).asList());
	}
	public interface IteratorSupport<T> {
		List<T> read(int index, int size);
	}

	public static <T> Iterable<T> iterable(final int initialStartIndex, final IteratorSupport<T> support) {
		final int STEP_SIZE = 100;
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					private int startIndex = initialStartIndex;
					private int current = -2;
					private List<T> buffer;
					private T nextOne = readNext();

					@Override
					public boolean hasNext() {
						return nextOne != null;
					}

					@Override
					public T next() {
						T result = nextOne;
						nextOne = readNext();
						return result;
					}

					@Override
					public void remove() {
						throw new RuntimeException("Not implemented.");
					}

					private T readNext() {
						if (current == -1) {
							throw new RuntimeException("Already returned returned null once.");
						}
						current++;
						if (buffer == null || current >= buffer.size()) {
							// last one consumed so read again
							buffer = support.read(startIndex, STEP_SIZE);
							startIndex += buffer.size();
							if (buffer.size() == 0) {
								nextOne = null;
								current = -1;
							} else {
								nextOne = buffer.get(0);
								current = 0;
							}
						} else {
							nextOne = buffer.get(current);
						}
						return nextOne;
					}
				};
			}
		};
	}
}
