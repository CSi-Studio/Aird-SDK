using System;
using System.Collections.Generic;

namespace AirdSDK.Beans.Common
{
    public class MrmPair
    {

        /**
        * order number for current spectrum
        */
        public int num;

        public string id;

        public string key;

        public WindowRange precursor;
        public WindowRange product;

        public string polarity;

        public string activator;

        public float energy;

        /**
         * cvList for current scan
         */
        public List<CV> cvList;

        public double[] rts;
        public double[] ints;
    }
}

