package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * represents an element of a distribution loaded from the file system
 * 
 * @author Tim Hilgert
 *
 */
public class ModelDistributionElement {
	
	int amount;
	int amount_sum;
	double share;
	double share_sum;
	
	public ModelDistributionElement(int amount, int amount_sum, double share, double share_sum)
	{
		this.amount = amount;
		this.amount_sum = amount_sum;
		this.share = share;
		this.share_sum = share_sum;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @return the amount_sum
	 */
	public int getAmount_sum() {
		return amount_sum;
	}

	/**
	 * @return the share
	 */
	public double getShare() {
		return share;
	}

	/**
	 * @return the share_sum
	 */
	public double getShare_sum() {
		return share_sum;
	}

}
