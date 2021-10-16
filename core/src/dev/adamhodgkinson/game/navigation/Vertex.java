package dev.adamhodgkinson.game.navigation;

import java.util.Objects;

public class Vertex {
	public final short x;
	public final short y;

	public Vertex(short _x, short _y) {
		x = _x;
		y = _y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Vertex vertex = (Vertex) o;
		return x == vertex.x && y == vertex.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
