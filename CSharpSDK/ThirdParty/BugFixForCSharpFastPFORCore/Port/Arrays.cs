﻿using System;
using System.Linq;

namespace CSharpFastPFOR.Port
{
    public class Arrays
    {
        private static void rangeCheck(int arrayLen, int fromIndex, int toIndex)
        {
            if (fromIndex > toIndex)
                throw new ArgumentOutOfRangeException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

            if (fromIndex < 0)
                throw new ArgumentOutOfRangeException("fromIndex");

            if (toIndex > arrayLen)
                throw new ArgumentOutOfRangeException("toIndex");
        }

        public static void fill<T>(T[] array, int start, int end, T value)
        {
            if (array == null)
                throw new ArgumentNullException("array");

            rangeCheck(array.Length, start, end);

            for (int i = start; i < end; i++)
            {
                array[i] = value;
            }
        }

        public static T[] copyOf<T>(T[] original, int newLength)
        {
            T[] copy = new T[newLength];
            Array.Copy(original, 0, copy, 0, Math.Min(original.Length, newLength));
            return copy;
        }

        public static void fill<T>(T[] array, T value)
        {
            fill(array, 0, array.Length, value);
        }

        public static void sort(int[] array)
        {
            Array.Sort(array);
        }

        public static string toString(int[] ints)
        {
            return string.Join(", ", ints);
        }

        public static bool equals(int[] first, int[] second)
        {
            return first.SequenceEqual(second);
        }
    }
}
