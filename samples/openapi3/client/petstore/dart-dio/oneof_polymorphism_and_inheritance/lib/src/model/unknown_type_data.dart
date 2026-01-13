//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'unknown_type_data.g.dart';

/// Represents a discriminated union variant that the client doesn't recognize.
/// Allows graceful degradation when the server returns new types.
@BuiltValue()
abstract class UnknownTypeData implements Built<UnknownTypeData, UnknownTypeDataBuilder> {
  /// The raw serialized data, including the discriminator field
  Map<String, dynamic> get rawData;

  UnknownTypeData._();

  factory UnknownTypeData([void Function(UnknownTypeDataBuilder) updates]) = _$UnknownTypeData;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UnknownTypeDataBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UnknownTypeData> get serializer => _$UnknownTypeDataSerializer();
}

class _$UnknownTypeDataSerializer implements PrimitiveSerializer<UnknownTypeData> {
  @override
  final Iterable<Type> types = const [UnknownTypeData, _$UnknownTypeData];

  @override
  final String wireName = r'UnknownTypeData';

  @override
  Object serialize(
    Serializers serializers,
    UnknownTypeData object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = <Object?>[];
    object.rawData.forEach((key, value) {
      result.add(key);
      result.add(value);
    });
    return result;
  }

  @override
  UnknownTypeData deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final serializedList = (serialized as Iterable<Object?>).toList();
    final rawMap = <String, dynamic>{};
    for (var i = 0; i < serializedList.length; i += 2) {
      rawMap[serializedList[i] as String] = serializedList[i + 1];
    }
    return UnknownTypeData((b) => b.rawData = rawMap);
  }
}
