package commandLineApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import static com.corrigal.fixCracker.CommonConstants.SOH;
import com.corrigal.fixCracker.FixUtils;

public class CheckSumCalculator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Paste FIX message up to last SOH before checkSum field (pipe delimited):");
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String fixMessageString = input.readLine();
		String calculatedCheckSum = StringUtils.leftPad(FixUtils.calculateCheckSumFor(fixMessageString).toString(), 3, '0');
		String calculatedCheckSumField = "10=" + calculatedCheckSum + SOH;
		String completedFixMessageString = fixMessageString + calculatedCheckSumField;
		if (FixUtils.messageIsValid(completedFixMessageString)) {
			System.out.println("Check sum = " + calculatedCheckSum);
			System.out.println(completedFixMessageString);
		} else {
			System.out.println("This is not a valid fix message : " + completedFixMessageString);
		}
	}
	
}
