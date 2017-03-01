package com.nc.http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.nc.http.html.HtmlCssStyle;
import com.nc.http.html.HtmlElement;

public class PlainHtmlHandler extends HttpHandler{

	private String response; 
	private String title; 
	private List<HtmlElement> elements;
	
	public PlainHtmlHandler(final List<HtmlElement> els, String title) {
		super();
		this.elements = els;
		this.title = title;
		
		response = buildHtml(new ArrayList<HtmlElement>(els));
	}


	@Override
	public void service(Request request, Response resp) throws Exception {
		resp.setContentType("text/html");
		resp.setContentLength(response.length());
		resp.getWriter().write(response);
		
	}	
	
	
	/**
	 * @param els
	 */
	public void update(final List<HtmlElement> els){
		response = buildHtml(new ArrayList<HtmlElement>(els));
	}
	
	/**
	 * updates Html page using existing HtmlElement list
	 */
	public void update(){
		response = buildHtml(new ArrayList<HtmlElement>(elements));
	}
	
	
	private String buildHtml(final List<HtmlElement> els){
		StringBuilder resp = new StringBuilder();
		resp.append("<!DOCTYPE html>\r\n");
		resp.append("<html>\r\n");
		resp.append("<head>\r\n");
		resp.append("<link rel=\"shortcut icon\" href=\"data:image/png;base64,iVBORw0KGgoAAAANSUh"
				+ "EUgAAALoAAACGCAIAAADl1AdwAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAD"
				+ "sMAAA7DAcdvqGQAABd+SURBVHhe7Z2HWxTX+sfvX5Kb5N77u4mJyU27ab8kJqaqiRV7bzEasbfYwBb"
				+ "sxoYGpIgiiCICVlQEaYKAKBYUkF5kKQvsLlu932XO5S5nZmHObBuW+TznyePsnB0yO5953/ecmd35y"
				+ "wsFBdEouigwoOiiwICiiwIDii4KDCi6KDCg6KLAgKKLAgOKLgoMKLooMKDoosCAoosCA4ouCgwouig"
				+ "woOiiwEBf0qW8UfNM1U4W+j4Wncr4IOyFxUyW+wJ9Rhe48sOBW1mljWS572PRPtdGvm/I2/fCYiIvy"
				+ "Z6+oUtFk/bHQ2mvbbyc6VW6NGhPvqM59orh7oG+Ykwf0KW6mbjijbq8qwl+yWpM3h99IivJXZeaFt2"
				+ "w/7pi1aVERVb0fSxaFdEFLeRVQ+5u+Rsja11qW3QjAtO7XEHL8DZd3iO6dMUYs5GsliXy1aW+tWP00Q"
				+ "xbV9DSi71IF50Kpe7/dOGMyd8v5zpGprrUqXUjj9CuvOF/5W5lM+nhBRg1ukSfbrpYjXlZzllJjrqo2"
				+ "vT8uAJXgtNKTWYL6eQVWDT1uvM/0sZY65hd8jRGdrrAlfHBWXxXDt8sNlu8yhUOqzFxw2hjEGOQleRX"
				+ "x8hLF1W7fgLPlQF+XusKh0VTJxBjYIz8ZvBkpAtc4ccVqyspxV6Wg/hYtM918cN5xrxiuLNTVsbIRZd"
				+ "mjWHSsduUK8hBf1x/6vWucFiNEa5jUPnKxRhZ6NKiNUwPy6ZcQVzZlVTUT1zh6KxjhtLGcHWMPIzxvC"
				+ "5wZRrPldf9Lu+59qRfucLRGWP4WYkbXXveGA/rokZcCc/hu7I7qcho6neucFhjDL+OQVaSQR3jSV1ad"
				+ "cbZEXcoV5CDtl161A/jii0WbYPQ6PoVQ+4ez46uPaZLW4fx55O5lCuIK1suPuy3ccUW4Rm84L96dnTt"
				+ "GV0QV+aeEHAFcUVxpQtrjIkfQRuDOgZZyUMxxgO6aPSmXyJzIQflyqYLD/XGvnQnohuQWx3jbl20etP"
				+ "CqHxbUThX1p4vNJgUVwSwU8dgrOSBOsatuiCu+EbTrqCtU1zpkc6sxIsxqGNyd7nZGPfpgriy+PRdKg"
				+ "dxrugMspiDkjPW+ZiEUbQxGCvl7HCnMW7SpcNoXhJDu4LFVbH3FFdEInxdCXUMjHFXHeMOXVDAroy9Zy"
				+ "sK15bGFEAj0kkcZrP35Cyj0dTWriEL4rBoVQJ1jDUr7XGPMS7XBcFj5VkBV1acZXbl8o20a6lZZKHvo9"
				+ "Hqfl29taKqliyLw2pMwkgBY9ySlVyrC4RYfe7eAL8rlCvLzhSg7CWdRGCxWJJSMib9suq5qom81PfBTq"
				+ "Vm5k74eQWzMRhdC9cx211tjAt1MZosGB7z65Vfo/JR9pJO4kBQmThvZXXtc7LsRaRk3sGusceYBt35nw"
				+ "SMcfF8jKt0wcB4Q3yhrShcW3Aqj7W2vZF2G6dgVU0dWfY6YIyUGIOsJDC6fsmlc74u0QW17caEB/wx88"
				+ "Ko/PYOtj1JycgZP3d5ZbXXusJxKysXuyklxiSMpnSxzuC5LCs5XxfkIP/Eh/x6ZX5kXquOYR+41D52zr"
				+ "LyqhrykveCnYUxiDGsOys8H2MdXQe4whgn62IyW7ZdesSPKzOP5zDVtiAjJ99n9tJnFdVkuR+A00NKjN"
				+ "GphO8Mt9YxTp53cKYuiCtwhRIFbU7EHdYcRFwpryLL/Ya023kwpqyS7SSx6BoFYgzqGGfHGKfpAle2X3n"
				+ "Mz0FwpUVrIJ3EkZ1332fW0qel5WS5nyG1jnmuSxxD6WIdK2X/7kRjnKMLctDOq0Vv+NOuTA/LZnUlJ79w"
				+ "9MzF/dYVjvTs/HFzlrPXMah8eTN4nDFOGl07QRe4svfaE369MinkNlNtC+4UPBgzc0lR8TOy7DB6g6FD3"
				+ "62hqCTrJNHRoW9Wt9q2do0Wr5tMJm5R3dqm03VwnR2BizHMxiAr8e8Mt875bneKMY7qAlf2XRdwZWpodr"
				+ "OGLa7kFjxEDnr0pJQsO0xJWeWnw6a89/VY23bpehpZLYngE2ffHTzWti3buAuvFzwowsa5V/AP/F2Ehw3"
				+ "bDyVeTalvkPgTRqhjMFZiLfatlS8/K8EYZ2Qlh3SBK/tvPOXnoMkht1VtetJJHHcLHyMHPXhcTJadwbHI"
				+ "2Dc/H0m1xeu2mx24b/zo8Rhqg4vWBuB16DLwi1HUKq599tO0zbuPSptmlBhjrHUM/7cdkJW2OWiMdF0Q1"
				+ "A/fLOa7MjYoU9XO5sq9h09GTl/kXFeQdHxmL6OOHNonQ6c0NEr/1Q8JunDt8+HTrySnS0iFqGMkTD51zv"
				+ "ny7/PtNOaF9LNFoi6IK0dTSyhR0OBKk4bZFcQV/JcsO4nHT5+9M9iHOmZcO3fxOunEjmRd0N7+ckxM/BV"
				+ "uO0zAGMQY5qzU0Sw8unYgxkjRhXOFH1fGBWU9b2Wr8hBRUK/k3XtIlp3HgWOR1NHqavOWb5Jc8NrTpayy"
				+ "ZqX/nhX+u5du2DFrycahkxaggqF6on34/cTcggfcpphAHTNx3kpmYxBjEsdSulhn8KzGsFWWHMy6mC2W4L"
				+ "TSNzfRrvj8mVmr1pFO4kBVO2rGIlS4ZNl5GAzGEdN8qUPV1T4eMlnyBUt7ulBgcPToaSlKFlS+VP+pC37D"
				+ "MIr0Y4GLMcwzeBhdC87H3N4qIcaw6YJzMiyjjB9Xhh9OZ40rGC2jXsHImSw7lcLHxdRBolpU3CXSlRGRun"
				+ "AghiWlZFJh5l9fjcnOv096MJKRY61jmI3paBL+vtLtLaxXCRh0QVwJzyzjz9uOCEyvZ3QFhQVyUHaexE+"
				+ "tV3YcDLE9QvyGU5x0ZYRJF45dh8Oot+AVso4dGIPRdWl5JVkWh9UYwdE1jGGJMWJ1gSsRWeUDN13lu1LV"
				+ "bJ2nEs/T0nK4gt0my86mXaNF6UAdIaq99804VBvkDSxI0OVhUQlVdM9estGR2UJuPkZSVuLXMchKMEZsH"
				+ "SNKF+xaZHb5W5tpV4YdSqtsYnMFpwXqFde5AhDq3xrUbZCCIcmnw6bYvoIWHn2evIEFCbo0NrUMGjHD9i0"
				+ "jpy92ZO4HSMxK1vt8+ffHdBojbs63d13gSlROBb9e+eHArdoWttq2pKxyzMwlODnIsmvw2xloe2zQpixYs"
				+ "zswnHpx4rxVEr5XIEGXtnbN12Pm2L5l+FRfB3UBmXcKxs1ZznrR3qJXC94Zrs/aLMaYXnRBDjp9p5I/Dhp"
				+ "6MK28ke1LD4gr2L2b6Tlk2TW0trVTxwYtPDoeI3ZqkIIIJOEGCQm6qJqavxjeLbpM913nSDLqAmMljK6Z6x"
				+ "jrVQKh0fXtzb1mpZ50wR6dyav615YkypUhB26VNrA9V6iyug71ihu+9pGcnk1lIoxEauoa9AYDf5L3SPhp8"
				+ "jbRSNClqLiMMnXHwVCyzmG4a9cS6piOC/bqmJ4q3550ibtbzXflm30prM+gqqqtR71yI+02WXYlq7fssz0wa"
				+ "DiVuVWBYdHUqrFzlnfo2WarJOgSEnnOtj/0lTZTZ4+s3AKkeGZjEGMEvn3ysjUr2b9KIKwL4sr5gmp+DoIrr"
				+ "DmooqoWRdm1lEyy7EpQJXz0wyTbY4MWk3CVW1taXoVDZbsKJTDrhSpWXTD++uzHabb956/a4pRMZAuMQYxB"
				+ "aUiWxWGtY4RG1/pMf3sxRkAX7EvCvRp+XPn2j9Sn9W2kkziqauqQXC9eS3X6ByTIxWu3qAs3nwydgkzErTU"
				+ "YjJN+WW27Fm3v0QhurUh61QV7igoauQ8DoqSUzO/Hz7Pt/K3P3DLX3H3M1THMxmhVQlnJbh1D64LDevF+7b"
				+ "tbaVcG7015XNdKOomj/rkKyicmpbjHFYw1Fq/bbnts0HAqk9WdUHkB7acpC5nykT1dnpaWY6g14eeV4+euQJ"
				+ "E0YtoiDJ4pd6GO06+k2iJ9dC00H2PNSrwYQ+uSXPScH1c+DLj+hDGutGu0cCU67pJ7XAE4mz/8fqLt4UFLvJ"
				+ "pCVndSXVv/1qDRth2slQTLBU57uvR8RRojI5S3+D/kNuIijEbTwjXbDoWcIstisRgLg2ldgl/SnRmM+oZ0+S"
				+ "+0LhVNWgySKV3e8L8SnllmYpkqwLl+4kzizEXrm5rV5CUXczYxiTpInw+fzr+1ZeqC36huAfuPkXUikKbLO4"
				+ "N9kAej4y6rW9nOOvEg/W3d+6f/rkDG65cW46OTmtC/C7iiFrgFVqB2KVNpbJ9nx7WBm67CGKbnOCCuhEef/3"
				+ "2T8oVbcwgS0sJ6dIdAV0A9xRevjHHs8pID3HgcwmLipu9ZEOL2lVnFUdldR1/TIRKk6y2oaK6lspZbw0alZV"
				+ "7j6zuDXu64JQ4k5CEUVhXC4+OR9waNvlXSqN/fzcBJTm3NaeADxmurA840KFnuzHNWBzHjyvaqE8saru3Swv"
				+ "rAmpadEN4xgzws2Yl1hhz8mzitF9/c6kxETEJtocEDZmIu0efz+wlG6nOfjsOizzj7eliD4S92AvXKJXf+3r"
				+ "snbvOmXpBXNlxMGRdwAHxAbITi/FprCbkb7Qrpz8324krHHZ1AYgxXU/k7WpvbpJSx8AYxBgXZSVsf/L8Nbb"
				+ "HA219wEF7BiAMUJ2/GjXLnlsUrLoA/G9cS82iLkr7zFoq8i/2AFzZHRi2Zus+xhz0wupK2Gs8Vz4zNxWRHnbo"
				+ "SRcAY5xYx8xd5ueKyrf4WcX73463PRjIL7fsX8isqWv4ZCh9gTo1M5es7hEJugDEmFWb91JvdOSWYYBt7j1yf"
				+ "PWWvaxfazKVxGvCX6ddifrI3PSY9LBPL7qAqmYtPyshxoRlsNUx4ERMAsZKzWq2+ZteCYo4Qx0JRIseTjhEowW"
				+ "rt1JvWbNlH1ndI9J0AXn3HlFvnLVkI1nHDk4/uLJq0x5mV8oua0L/Qbty6kMxroDedQHVzVp+jEEdA2NYY8yp2"
				+ "IvTfdc2tTgtxmCbI6cvpo7Etn1BWIVojxKYapysiVdTqLd8OXKmmFwpWRfUFh98O8H2jYNHz25tY7v6xoGh8v6"
				+ "gkyv8d3V0sNW2pmcXBXJQ1MfmRrEzT6J0AchKPx3u9gRwNC7GsNYxkbEXUGw6q44pKi7jX4LOyS/Eqr1HI1BjU"
				+ "s13bQDCOHzFv23fhXYlOYPbZg9I1gVa/zD+F9s32l6gEA9cORRyatnGnRLiivb4G7Qrpz4yN4gdFQKxugDBGTzU"
				+ "MTCGJcRYiYiJhzFOiTE4z2yPAdrQSQtw2qEM/G5ct+s1XMMpXl1bD2uXbthBrcIrZKP2cUSXoRPn274RulTXsf"
				+ "3aHjZyODRqyfrtrGWyqeKGNnwA7crJd8wqttulGXQBtWqd4JzvsfRnrMY4JStBi+FT6S+I7DlyHKtu592n5vu7"
				+ "Gnfb5ZXkdGpG5NNhvX/BUbIuiApUPBs0YgZT4Q/FA8OiF68L0OrYbmI0VSYLzMVFvs8UVzjYdAGofPlZCcaEZj"
				+ "xjzUrRcZdQ+TqSlfLvP6IO+dtfjinsvCdhx8FQDJcE2wzf9Th4OEGp8RE2FXfpBrdle0jW5WFRCfXGEdN8xU/Y"
				+ "o+efETG+a3/XaBldQVw5/ibPlQ/M9VJugWXWBSArDecZg6wEY1grXwfrmID9x6hj4DNrKVcA1jc0llXWCLaqmj"
				+ "qUL+jDH9z+snJz54btIk0X7Onm3UepN64POEhW9wZOreATZxeu2cb6K9+mypvaiLdpV06+a66TeAusFF0AYgx/"
				+ "rITKF1mJ9BANjJm1eL2ErITPbsiEbtUAGsI1WS2CG2n0nZrvDh7bc4KQpktGzl3qy2mIZOnZYr8Ocexk7ILVW5"
				+ "hdqckUiCsRA6XFFQ6JuoA6tY5vDLJScFopa4yJPn952sLfWI3Bx00dbCwy3a0teB94dNxlsloIVl0wfjmTkPTxk"
				+ "MnUu6b+ulbMfTaIK6FRcfNXbWZ3JUPAFdS2UuMKh3RdAGLMiECB0XVIOnMdc/r8FdQxqiaGn9LYsP0QdQwkfBeE"
				+ "vxEkxx5KCnu6YEi8J/D47sDwrrbjYMhK/z3fj59HOY323jfjCh70Mt0OsC/HT8fPW+EvxZWT7/Bdweukh1Qc0gV"
				+ "UCtUxnDFMYyXEGIyVxF9Xwif45ciZ1GEIPRVHVosGIYoqlpE1MNImq3nY00XkDQxoyHddtw/3AD6QE2cSf17uz3"
				+ "pp1lyXLeBKxEBTdTrp4QCO6gJqW3SCY6WgW8w/G4asNMN3nRhjrqVmUWctTtmKarZfiwTQjrqdFi08Op6s5uGgL"
				+ "l8MnyFmMhCciEmYu8yP2ZX6PG3EW7Qrx99AHUN6OIYTdAG1ah0/xkioY5CVYuKvWOuYHutNnHnL/XZRR0Lyd722"
				+ "7guiNjV5/hp7+UiyLv8/bOqm3UfETOPiQ4g6dwk5kfXimrnujlBcedtU1e0OVEdwji7AXh2DsRJrHQNjeq5jUD9"
				+ "+6zP302FTbJuYCC9I7r2H1KYGj56N8TZZ3Z3w6PNUZ+7aZOHjYghBrfrsx2kIXcs27jqbmCRyvr+rjGN2BXEl8t8"
				+ "8VwaaKnqZSWLCabqA6mbhOgYxhvQQDTeDZy/GoAZElKaa+CkvCsQkalNoRqPw1jCcoXpy82b46+rWbq9jEatYA96"
				+ "ZhKsi07EtZtV9beQHlCuasNdMlcmkh5Nwpi6gvrWDH2OQlY6k9nSPliA4I5GVHJnz7VtALM4V1luCzKoH2hO8HBQ"
				+ "+wOmuACfrAlD5ChqDGMOalWDMdN+1/cEY7Gzcpes4PRoa2R4HZ40rpz6iXUFtW9bT7JFknK8LsFfHSJjBwwnHOh/T"
				+ "58Buxl9OnrJgNbMrjQ+1UZ9QrmjCXzc9u0h6OBuX6ALsxZijqSWswxfEGIRoLzYGrkxdsIb1MZPmpiJtFB1XNKH/"
				+ "56K4wuEqXUAPdQxrjIm9cA1ZyfuMwa4lJqUgrtQ/p78v2DPm5icCriCulF4gPVyDC3UBNS26kUcy+MZIqGNgjPfF"
				+ "mKs3rY+vrZPgyunP+a4Yi5kntVlxrS4AxtirfJmykjXGJCaxfwFY1uw8FFpbz3b/paWlRHv6C9qV0L8bi8+RHq7E"
				+ "5bqA560do4RizOGUYtY6hnWQKXNYL4ha1KXa6E/5rphK7F61cC7u0AXAGHuVL1NW6s9Y1M+0MYNoV8L+aXzC/JNpk"
				+ "nGTLgBjJcEYE3SLrY7pn1jUZbqzX9OuhP7D+BjZ2X2fnvt0AbVq3eijAsZgrMSalfoVlrZKAVdCXjUWRbnTFeBWXQ"
				+ "Cy0hghYw7dZK5j+gmWtmrdma94rvzNzXGFw926gHo7la9Sx/CxuhL7He0KctCDcPe7AjygC0BWslfHMM3geTcWTa3"
				+ "u3BDaFeSgwhCPuAI8owtAVvL5M5NvzOGbxYoxoNOV7wVc8VBc4fCYLkCwjnlzk7WO6efGWJ+9ee4HAVcKg1mfQORc"
				+ "PKkLQB0jOFb6sx+Prq2uxPOeBR3yqqEg0LOuAA/rAurUOsGxEuuVSO/A7hNaZeAK8LwuoKGtY2yQQB1zMLl/ZaVOV"
				+ "3hx5djLhntH5eAKkIUuAMYIxhhUvv0kK1l0jQLPdEAOyt8vE1eAXHQBqGP4xgzw6xfzMZaOJuEnxuTulo8rQEa6AM"
				+ "GsBGMwVvJiY+y48rIhb98Lcc+8cxvy0gVgdD0uKIsyBlnpQPJTrzTG0tHScWGcgCu5eyQ859nVyE4XoGrTC9cxKd4"
				+ "WYyx6dcelyTxXXjFk/y63uMIhR12A4JwvslJkdjnp4RXoMzfSrlif7Szl+fHuQaa6AMSYCcF0VvotzlVPKvcI+uSFt"
				+ "Cs5AbJ1BchXF4DKd3x3Y9ac8y5dbi76nyvinrXqWWStC0CMsc1Kq88x/1ijnNHfXNzlij5jvZzjCofcdQEwpivGeJ0u"
				+ "Szp1+as+Y4P8XQF9QBcAYyYesxqzKta7dElZSlwxsf1cu6foG7oAGDMuKGull+mSulyftqavuAL6jC5A1a6PyPKqgb"
				+ "Sp+pbMa1uKvqSLgsdRdFFgQNFFgQFFFwUGFF0UGFB0UWBA0UWBAUUXBQYUXRQYUHRRYEDRRYEBRRcF0bx48R/FHkxg"
				+ "2YmLfAAAAABJRU5ErkJggg==\"/>");
		
		resp.append("<title>");		
		resp.append(title);
		resp.append("</title>");
		
		resp.append("<style>\r\n");
		Set<HtmlCssStyle> sm = new HashSet<>();
		for (HtmlElement e : els) {
			List<HtmlCssStyle> styles = e.getStyles();
			for (HtmlCssStyle s : styles) {
				if (sm.add(s) == true) {
					// if there were no styles with this id
					resp.append(s.cssStyle());
				}
			}
		}
		resp.append("\r\n</style>\r\n");
		resp.append("</head>\r\n");

		resp.append("<body>\r\n");
			
		for (HtmlElement e : els) {
			resp.append(e.getHtml());
		}
		
		
		
		resp.append("<script>\r\n");
		
		
		
		els.stream().map(HtmlElement::getScript).filter(s -> s != null)
				.forEach(resp::append);

		resp.append("\r\n</script>\r\n");
		
		resp.append("\r\n</body>\r\n");
		resp.append("</html>\r\n");
		
		return resp.toString();
	}



}
