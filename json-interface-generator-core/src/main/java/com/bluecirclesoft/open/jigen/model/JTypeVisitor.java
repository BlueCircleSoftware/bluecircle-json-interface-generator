package com.bluecirclesoft.open.jigen.model;

/**
 * TODO document me
 */
public interface JTypeVisitor<ReturnType> {


	ReturnType visit(JObject jObject);

	ReturnType visit(JAny jAny);

	ReturnType visit(JArray jArray);

	ReturnType visit(JBoolean jBoolean);

	ReturnType visit(JEnum jEnum);

	ReturnType visit(JNumber jNumber);

	ReturnType visit(JString jString);

	ReturnType visit(JVoid jVoid);

	ReturnType visit(JSpecialization jSpecialization);

	ReturnType visit(JTypeVariable jTypeVariable);

	ReturnType visit(JMap jMap);
}
