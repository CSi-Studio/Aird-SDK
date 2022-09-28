from setuptools import setup
from setuptools import find_packages


VERSION = '0.1.0'

setup(
    name='AirdSDK',  # package name
    version="0.0.1",  # package version
    url="http",
    author="CSi-Studio",
    author_email="csi@csibio.net",
    maintainer="Miaoshan Lu",
    license="Mulan PSL v2",
    description='Aird SDK for python. AirdPro version > 4.0.0',  # package description
    keywords="AirdPro, Aird, AirdSDK, ComboComp",
    packages=find_packages(),
    zip_safe=False,
    python_requires=">=3.6",
    install_requires=[
        "pyfastpfor>=1.3.6",
        "Brotli>=1.0.9",
        "python-snappy>=0.6.1",
        "zstandard>=0.18.0",
    ],
)
