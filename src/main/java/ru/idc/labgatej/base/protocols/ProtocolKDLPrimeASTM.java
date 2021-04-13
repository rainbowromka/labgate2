package ru.idc.labgatej.base.protocols;

import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.model.KdlMax.TestResult;
import ru.idc.labgatej.model.KdlMax.parser.ASTMParser;
import ru.idc.labgatej.model.KdlMax.parser.ASTMResultParser;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ProtocolKDLPrimeASTM extends ProtocolASTM {

	private final String deviceCode;

	public ProtocolKDLPrimeASTM(String code) {
		this.deviceCode = code;
	}

	// 3O|1|20005317||ALL|?|20200516131727|||||X||||1||||||||||F<CR><ETX>1D<CR><LF>
	@Override
	public OrderInfo parseOrder(String msg) {
		OrderInfo result = null;
		final Pattern pattern = Pattern.compile("^\\d*O\\|(\\d*)\\|(.*?)\\|",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
			result = new OrderInfo((matcher.group(2)));
		}
		return result;
	}

	@Override
	public List<ResultInfo> parseResults(String msg)
	{
		ResultInfo result = null;

		ASTMResultParser resultParser = ASTMResultParser.init(msg);
		TestResult testResult = resultParser.parse();

		if (resultParser.getError() == ASTMParser.AstmParseError.NONE) {
			result = new ResultInfo();
			result.setResult_status("X");
			result.setNormal_range_flag(null);
			result.setTest_type("SAMPLE");
			result.setSample_type(null);
			result.setContainer_type("NORMAL");

			result.setDevice_name(deviceCode);

			String universalTestId = testResult.getUniversalTestId();
			String resultInSelectedUnits = testResult.getValue();
			String resultStatus = testResult.getResultStatus();

			String[] s = (universalTestId != null)
				? universalTestId.split("\\^") : new String[0];

			if (s.length > 0) {
				result.setTest_code(
						(s.length > 4) ? s[4] : null
				);
			}

			if (resultInSelectedUnits != null
				&& !resultInSelectedUnits.isEmpty())
			{
				String outOfRange = "";
				if (resultInSelectedUnits.contains("<"))
				{
					outOfRange = "<";
					resultInSelectedUnits = resultInSelectedUnits
						.replace("<", "").trim();
				}
				else if (resultInSelectedUnits.contains(">"))
				{
					outOfRange = ">";
					resultInSelectedUnits = resultInSelectedUnits
						.replace(">", "").trim();
				}

				resultInSelectedUnits = resultInSelectedUnits
					.replace("E+", "x10^").replace("*", "x");

				result.setResult(resultInSelectedUnits);

				if (!outOfRange.isEmpty()) {
					result.setNormal_range_flag(outOfRange);
				}
			}

			if (resultStatus != null && !resultStatus.isEmpty())
			{
				result.setResult_status(resultStatus);
			}
		}
		else
		{
			log.error("parseResultMsg: invalid message: " + msg + "\n");
		}

		return Collections.singletonList(result);
	}
}
