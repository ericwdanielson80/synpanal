package neuron_analyzer;

public class ColocalizationInfo {
double totalPixelsA;
double totalPixelsB;
double totalPixelsAB;

double integratedA;
double integratedB;
double integratedAwithB;
double integratedBwithA;

	public ColocalizationInfo(int tA, int tB, int AB, long sA, long sB, long sAB, long sBA)
	{
		totalPixelsA = tA;
		totalPixelsB = tB;
		totalPixelsAB = AB;
		
		integratedA = sA;
		integratedB = sB;
		
		integratedAwithB = sAB;
		integratedBwithA = sBA;
	}
	
}
