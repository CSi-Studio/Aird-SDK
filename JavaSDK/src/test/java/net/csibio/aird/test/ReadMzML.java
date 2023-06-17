package net.csibio.aird.test;

import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Base64;

public class ReadMzML {

    @Test
    public void readBase64String(){
        String mzOriString = "eJwN0WtU1AUexvFggOE2XEQxGJhUylaEQEEYyEs9P7oJAZ1m8z8tNpsjZcvEGWBktyRF5TKiI8Ep0joLq7YVRa5KWpCAI4uCu+eQS+UedYBhZiwY7reZAbR+Lz4vvm+f57hHNk54ZuOUVza+FGcjwVeBbX4KpPsrsEOiwK4ABfIDFXg3SIGKYAVqlynw9xAFXB074G3cgdCrO/Cpp4ALXgKuiAX0egsw+Qiw+wpw+QnwlggIDRDwaKCAhCABTwcLyFom4LUQAasTc7BxUw4oKQeK5BzkynNQnJKDytQcZLSooGpVofB7Fcovq3CiTYWmdhU6OlT43xUVHjWqkXJVjaxONXb/W413u9SovqbGP6+r0dqtxg89aqzfn4dnDuRBVZqHdw7m4YNDeTh7OA/dZXkYKueO1eBfT2jQE6eBNV6DBxs0CEvQIDFRg8xNGryVpEFZsgb1cg1aUjT4MVWDwo1aVCdo0ZSoRc8mLe4laSGSa7EqRYug8SLETRQhc7II+VNFMEwXoWmmCDPhOoRE6LAxUoeXZDoUPKLD+6t0sG7SQZSsQ5Rch0y3UpiZzr0UYlEpPmaxHqW4wvaZKhHSX4kv2YJIj1oPPaI99TAypZceU0wv1mOVtx7fsSwfPTavqMJPLD+0Cl4rq9DA5A9X4SZ7e9AAb7MBZ9jWIQP23KmGx91q/INt7K9BL9MM1MBnsAaD5hq8N1SDcEsNSlbVImx1LS6xV/3r4GInJXV4y6ce/r71OMde9qvH5+H1SJfWY5ylWE9hgJXZTsE83IiqkUYk2hvhOdCMb9iuwWYEm5vRwd4eaoYi/BJE0ktoZm3idmi92xHl045TEx3442QHfKY60MYKpjsgHzFijJ2xGyGMGiEZM+Iqi5kwwszqJo0IG+tELysf78STE52YYp9PduK1qU4sn+7EDVY604nAkS5cY+/Zu6Ac70LQRBeus8q7PXjK1AMXg2MAns4BdLMrv5pxeNiM50bMmBuxoNVuwf5RCzBmgXjcgvEYKy7EWvHXJ6x4Ms6Kh+KtkFlssLJGqw35NhsS7tngZEuTNhinbKiYtuHP8zY85rDBzs45bSi7a0eGyc7/23Ft3SiOR4/ilfWjiIwZhY2dG3Vh/5gL6eMuGB1uFO10o1r2yLw76dk0+5PDnWKd7vQRe8jlTkvTIsqdEVEvuzDjQRGzHlTBUhf96DMWsuRH52f9STbnT0eZk70x70997GaohLaulNBXbHBaQpkzEvqerZuVUB3zmJNQARtgGfMSamV/cEjoQyZySkjL+lnlbADNMfVcAN1k2+YD6GtW4hZMI0zpHkzCQjj9h21ZDKfdDindYtudUrrM4lxSOs1WLEhJzxZZ/qKUBtnLS1JKd0VQO9uwEEGfsk/mIynAEUmHGM3K6Fu2fk5GDSxkXkaVzOKQ0StOGd1gW10yusDWLsjoY5b78Bq6zbLC1vCma2iPYw2Z2AsL0XSVpS5G0y1XDGUtxFA3S1iKoSb22P0YylgeS11s64pYcrniqGghjsbYm4tysrLXl+R0X55K+1JSyckCH2whA/P/bQuVzW4n8dx2qmLS+y9SA4t68CIVibPIyUq8s0gUrqQqtkyqpJNsdYSSvmDxkUrqu62kV+8oaYj9tHYn7Xx8J91jBwJU5B2oolq2wbSXjOyl/r1ku11Me+8Uk9fdYqpja03F9C1b+aCEGtnm30poW8FBOsL6WGThQRIKD9FpNsZmCypoW2EFHWXD5ccoqeIYHWY/sOWeBnqdnWV1XgaysHixgZJ3N9ARdofF5jZQKUPpGapjw6xFf56CjpynN9h/LRcpynqR9rE+Fm27SDeqWijqaAvtYz+ymGMtVM5MLMnQQtXsF7b2aCsdYP9nc39po8y8NvqCrdvdToeZif2t4Ge6xZILf6b04/3UxCTV/ZTPeln8+/00bxgg4fgAtTD7vUHK+GWQzjLRLjPlsuuMfh2mz5jv8DANvWmnZ/fYqZH12ZcodXSJTrPb3W5pgT1uacSy833TKthlZtoelhaaHpaWxZ4fjkg7xC6zDx7fnNbHfgcnUnd4";
        String mz = "<binary>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</binary>";
        String intensityOrigin = "eJytVL9Lw0AU/qCLm4PUOaO6WHAR7BA0dwERyejo4FDQqYogFA3oIJ1cLOogBREc619gRkGEDg4uQszkJE4ODuK7JBev1ySmrRce9+7y7v343seD17JgBJHI1Z5kqJ0xHJc4/EMWnttk579GIpd4K9dTjSMwbdQdGxd3HE6V422co1tm8Hwr2f1TK9x1Hx2KIXTVp0exag8M9+/0r8LxsUp5kMhlVhm6Exw33ShHcRZ5tlvpOUpdvfP3Gb6uOEq7Nh6vbZc+bE3bGJvlwAyHd8JglCNJ8KH3lSWG9SpLzn3xCE9RU14eufkFVqad+OdQbG/7tzeI+6fmIuKHOQZWKi49/pVck3cpsVWe6PmtNf+uV31vKDUOjI+ChawdgXYv41AtjUWOnT3i5C3D1BFDhXjkCs5l8LlH3yjWRx3TNE7LO/293ps87hg53FB10cMifcjjWRq2qo0p9XMGZ45j/pPjsslhvhAvD+gurkPYCd3UYwX9uGXpLulCGpscKwscz98MnU7URze2c1OwSvOV4K3WGN87kvtB9iwpgl8Rm//S1XkTzurl4WaP1IX49Wz+yLnyly9hV2QOSqyGnQPq3O3ZB/SZlZeuj+JnxJ7/AHnYUcQ=";
        String intensity = "<binary>eJzNliESAjEMRfdoHBK5EoFArEAgEAgEAoFAIBEIBALBARiYfMGbySTd7gIxf5qm6W+Tpmmal0wnb2haIGVm+qXhxrCbfM5n/U0d/d70V8Ob4clwh/21b+uMySfiwXN4POVvZbgGvzmQIv0W67W/5iMeWb1np/gdDe+GD4wvhlug8mEG9Ph495I9B+Mc2Q+lZx6V+tM6nTt6N5E/5gl5Rfwiv9F+nl9K6fuXvgvma/f34jh2/ng8yDPiTVFcVH8OhqpPuk/ZsU5SSs+7GMgP8zPK01K7Wl59607fuuHp+T9k1w0V79K8zfrXf6I8Phsqn/VPsn7Snxd32pXWzVK9UH0N+xe9z755Wnvf2T6Ndb/2H8zaj+3n3/Ref+PF4Vu8mM8dxlk/7FdqefE/q/WX7b/G6ge89+mNh+LTV1/ap/yKz+/e+RPJr9X9</binary>";
        float[] mzsOrigin = parse(mzOriString);
        float[] mzs = parse(mz.trim().replace("<binary>","").replace("</binary>",""));
        float[] intsOrigin = parse(intensityOrigin);
        float[] ints = parse(intensity.trim().replace("<binary>","").replace("</binary>",""));
        System.out.println("HelloWorld");
    }

    public float[] parse(String base64){
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(bytes));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fb = byteBuffer.asFloatBuffer();
        float[] fs = new float[fb.capacity()];
        for (int i = 0; i < fb.capacity(); i++) {
            fs[i] = fb.get(i);
        }
        return fs;
    }
}
