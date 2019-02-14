package fr.profi.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.profi.util.StringUtils;

/**
 * Utility class to retrieve OS info of the running JVM.
 * 
 * @author LMN
 * 
 */
public final class OSInfo {

	/* Constants */
	private static final Logger LOG = LoggerFactory.getLogger(OSInfo.class);

	private static final String OS_NAME_KEY = "os.name";

	private static final String WINDOWS = "windows"; // Normalized to lower case

	private static final String LINUX = "linux"; // Normalized to lower case

	private static final String MAC = "mac"; // Normalized to lower case

	private static final String OS_ARCH_KEY = "os.arch";

	private static final String SUN_ARCH_DATA_MODEL_KEY = "sun.arch.data.model";

	private enum ARCH {
		AMD64("64", null), X86("32", "86");

		private final String m_sunDataModel;

		private final String m_archPattern;

		private ARCH(final String sunDataModel, final String archPattern) {
			assert (!StringUtils.isEmpty(sunDataModel)) : "ARCH() invalid sunDataModel";

			m_sunDataModel = sunDataModel;

			m_archPattern = archPattern;
		}

		public String getSunDataModel() {
			return m_sunDataModel;
		}

		public String getArchPattern() {
			return m_archPattern;
		}

	};

	/* Private constructor (Utility class) */
	private OSInfo() {
	}

	/* Public class methods */
	/**
	 * Retrieves type of OS (Mac, Linux and Windows supported) and architecture (amd64 or x86/i386) of the plateform of the running JVM.
	 * 
	 * @return OS Type and arch or <code>null</code> if it cannot be retrieved.
	 */
	public static OSType getOSType() {
		OSType result = null;

		final String osName = System.getProperty(OS_NAME_KEY);

		if (osName == null) {
			LOG.warn("Unable to retrieve \"{}\" system property", OS_NAME_KEY);
		} else {
			final String normalizedOsName = osName.toLowerCase();

			if (normalizedOsName.contains(LINUX)) {

				switch (getArch()) {
				case X86:
					result = OSType.LINUX_I386;
					break;

				case AMD64:
					result = OSType.LINUX_AMD64;
					break;

				default:
					LOG.warn("Unknown Linux arch (amd64 / i386) type");
				}

			} else if (normalizedOsName.contains(WINDOWS)) {

				switch (getArch()) {
				case X86:
					result = OSType.WINDOWS_X86;
					break;

				case AMD64:
					result = OSType.WINDOWS_AMD64;
					break;

				default:
					LOG.warn("Unknown Windows arch (amd64 / x86) type");
				}

			} else if (normalizedOsName.contains(MAC)) {

				switch (getArch()) {
				case X86:
					result = OSType.MAC_I386;
					break;

				case AMD64:
					result = OSType.MAC_AMD64;
					break;

				default:
					LOG.warn("Unknown Mac arch (amd64 / x86) type");
				}

			} else {
				LOG.warn("Unknown OS [{}]", osName);
			}

		}

		return result;
	}

	/* Private methods */
	private static ARCH getArch() {
		ARCH result = null;

		final String sunDataModel = System.getProperty(SUN_ARCH_DATA_MODEL_KEY);

		if (sunDataModel == null) {
			LOG.warn("Unable to retrieve \"{}\" system property", SUN_ARCH_DATA_MODEL_KEY);
		} else {

			if (sunDataModel.contains(ARCH.AMD64.getSunDataModel())) {
				result = ARCH.AMD64;
			} else if (sunDataModel.contains(ARCH.X86.getSunDataModel())) {
				result = ARCH.X86;
			} else {
				LOG.warn("Unknown SUN arch data model [{}]", sunDataModel);
			}

		}

		final String osArch = System.getProperty(OS_ARCH_KEY);

		if (osArch == null) {
			LOG.warn("Unable to retrieve \"{}\" system property", OS_ARCH_KEY);
		} else {

			if (osArch.contains(ARCH.AMD64.getSunDataModel())) {

				if (result == null) {
					result = ARCH.AMD64;
				} else if (result != ARCH.AMD64) {
					LOG.warn("SUN arch data model {} differ from OS arch {}", result, ARCH.AMD64);
				}

			} else if (osArch.contains(ARCH.X86.getSunDataModel())
				|| osArch.contains(ARCH.X86.getArchPattern())) {

				if (result == null) {
					result = ARCH.X86;
				} else if (result != ARCH.X86) {
					LOG.warn("SUN arch data model {} differ from OS arch {}", result, ARCH.X86);
				}

			} else {
				LOG.warn("Unknown OS arch [{}]", osArch);
			}

		}

		return result;
	}

}
