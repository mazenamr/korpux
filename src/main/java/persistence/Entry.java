package persistence;

public record Entry(
        String URL,
        int TotalCount,
        int AsTitle,
        int AsHeader1,
        int AsHeader2,
        int AsHeader3,
        int AsHeader4,
        int AsHeader5,
        int AsHeader6) {
}
