//javac -encoding UTF-8 harry/common/IdCreator.java
/**
 * 
 */
package harry.common;

import java.util.Arrays;
import java.util.Random;

/**
 * 64位ID生成器,最高位为符号位,始终为0,可用位数63. 实例编号占10位,范围为0-1023 时间戳和索引共占53位
 * 
 * @author 王海龙
 */
public final class IdCreator {
	private long timestamp = 0;// 当前时间戳
	private long index = 0;// 索引/计数器
	private int instanceID;// 实例编号
	private int indexBitLength;// 索引可用位数
	private long tsMax = 0;// 时间戳最大值
	private long indexMax = 0;

	private static IdCreator _default = new IdCreator();

	/**
	 * ID生成器
	 * 
	 * @param instanceID
	 *            实例编号(0-1023)
	 * @param indexBitLength
	 *            索引可用位数(1-32).每秒可生成ID数等于2的indexBitLength次方.
	 *            大并发情况下,当前秒内ID数达到最大值时,将使用下一秒的时间戳,不影响获取ID.
	 * @param initTimestamp
	 *            初始化时间戳,精确到秒. 当之前同一实例生成ID的timestamp值大于当前时间的时间戳时,
	 *            有可能会产生重复ID(如持续一段时间的大并发请求).设置initTimestamp比最后的时间戳大一些,可避免这种问题
	 */
	public IdCreator(int instanceID, int indexBitLength, long initTimestamp) {
		if (instanceID < 0) {
			// 这里给每个实例随机生成个实例编号
			Random r = new Random();
			this.instanceID = r.nextInt(1024);
		} else {
			this.instanceID = instanceID % 1024;
		}

		if (indexBitLength < 1) {
			this.indexBitLength = 1;
		} else if (indexBitLength > 32) {
			this.indexBitLength = 32;
		} else {
			this.indexBitLength = indexBitLength;
		}

		char[] cc = new char[53 - indexBitLength];
		Arrays.fill(cc, '1');
		tsMax = Long.parseLong(new String(cc), 2);

		cc = new char[indexBitLength];
		Arrays.fill(cc, '1');
		indexMax = Long.parseLong(new String(cc), 2);

		this.timestamp = initTimestamp;
	}

	/**
	 * 默认每实例每秒生成65536个ID,从1970年1月1日起,累计可使用4358年
	 * 
	 * @param instanceID
	 *            实例编号(0-1023)
	 */
	public IdCreator(int instanceID) {
		this(instanceID, 16, 0);
	}

	/**
	 * 默认每秒生成65536个ID,从1970年1月1日起,累计可使用4358年
	 */
	public IdCreator() {
		this(-1);
	}

	/**
	 * 生成一个64位ID
	 */
	public synchronized long create() {
		long id = 0;

		long ts = System.currentTimeMillis() / 1000;
		ts = ts % tsMax; // 如果超过时间戳允许的最大值,从0开始
		id = ts << (10 + indexBitLength);// 腾出后面部分,给实例编号和缩引编号使用

		// 增加实例部分
		id = id | (instanceID << indexBitLength);

		// 获取计数
		if (timestamp < ts) {
			timestamp = ts;
			index = 0;
		} else {
			if (index > indexMax) {
				timestamp++;
				index = 0;
			}
		}

		id = id | index;

		index++;

		return id;
	}

	/**
	 * 获取当前实例的时间戳,精确到秒
	 */
	public long getCurrentTimestamp() {
		return this.timestamp;
	}

	/**
	 * 默认每实例每秒生成65536个ID,从1970年1月1日起,累计可使用4358年
	 */
	public static IdCreator getDefault() {
		return _default;
	}
}
