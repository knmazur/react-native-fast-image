// @flow
import type {ViewProps} from 'react-native/Libraries/Components/View/ViewPropTypes';
import type {HostComponent} from 'react-native';
import {Int32} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';


interface NativeProps extends ViewProps {
    tintColor?: Int32;
    resizeMode: string;
    source?: Readonly<{ uri?: string, headers?: Readonly<{}>, priority?: string, cache?: string}>;
}

export default (codegenNativeComponent<NativeProps>('FastImageView') as HostComponent<NativeProps>);