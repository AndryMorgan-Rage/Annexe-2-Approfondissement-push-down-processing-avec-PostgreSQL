package Model;

public class VoteTypeCount {
    private voteType voteType;
    private long count;

    public VoteTypeCount(voteType voteType, long count) {
        this.voteType = voteType;
        this.count = count;
    }

    public voteType getVoteType() { return voteType; }
    public long getCount() { return count; }

    @Override
    public String toString() {
        return "VoteTypeCount(voteType=" + voteType + ", count=" + count + ")";
    }
}
