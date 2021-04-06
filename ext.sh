# ext folder
if [ ! -d "ext" ]; then
  mkdir -p ext
fi

# alchitry loader
if [ ! -d "ext/alchitry-loader-1.0.0" ]; then
  wget https://github.com/alchitry/alchitry-loader-gui/raw/master/build/work/alchitry-loader-1.0.0-windows.zip
  unzip alchitry-loader-1.0.0-windows.zip -d ext/alchitry-loader-1.0.0
fi

# Saxon SoC
#if [ ! -d "ext/SaxonSoc" ]; then
#  git clone git@github.com:SpinalHDL/SaxonSoc.git ext/SaxonSoc
#fi
