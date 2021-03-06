public class BithattenTarif {
	public static void main(String args[]) {
		final int netSize 		= Integer.parseInt(args[0]);
		final int start 		= Integer.parseInt(args[1]);	//13
		final int end 			= Integer.parseInt(args[2]);
		final int unitPositionStart	= start%10;			//3	Einerstelle
		final int unitPositionEnd	= end%10;
		final int decileRankStart	= start/10;			//1	Zehnerstelle
		final int decileRankEnd		= end/10;	
		int price 			= 0;
		int outerCirclePrice 		= 0;
		int[][] streetNet 		= new int[netSize][netSize];
		
		for(int i = 0; i < netSize; i++) {
			for(int j = 0; j < netSize; j++) {
				streetNet[i][j] = i*10 + j;
				System.out.printf("%d\t", streetNet[i][j]);	//show the full array
			}
			System.out.printf("\n");
		}	
		//calculate prize for perpendicular street	
		if(decileRankStart <= decileRankEnd) {
			for(int i = decileRankStart; i < decileRankEnd; i++) {
				price++;
			}
		} else {
			for(int i = decileRankEnd; i < decileRankStart; i++) {
				price++;
			}	
		}		
		//calculate prize for horizontal street
		if(unitPositionStart <= unitPositionEnd) {
			for(int j = unitPositionStart; j < unitPositionEnd; j++) {
				price++;
			}
		} else {
			for(int j = unitPositionEnd; j < unitPositionStart; j++) {
				price++;
			}
		}	
		
		//check for cheaper price
		if (decileRankStart <= unitPositionStart) {
			outerCirclePrice = decileRankStart + netSize-1 - decileRankEnd;
		} else {
			outerCirclePrice = unitPositionStart + netSize-1 - decileRankEnd;
		}	
		
		if(price <= outerCirclePrice) {
			System.out.println(price);
		} else {
			System.out.println(outerCirclePrice);
		}

	}
}


